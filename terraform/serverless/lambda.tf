resource "aws_security_group" "lambda" {
  name        = "${local.name_prefix}-lambda-sg"
  description = "Security group for the policy API Lambda"
  vpc_id      = data.aws_security_group.rds.vpc_id

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = local.common_tags
}

resource "aws_security_group_rule" "rds_from_lambda" {
  type                     = "ingress"
  description              = "Allow the policy API Lambda to connect to SQL Server"
  from_port                = data.aws_db_instance.policy.port
  to_port                  = data.aws_db_instance.policy.port
  protocol                 = "tcp"
  security_group_id        = local.rds_security_group_id
  source_security_group_id = aws_security_group.lambda.id
}

resource "aws_cloudwatch_log_group" "lambda" {
  name              = "/aws/lambda/${local.lambda_function_name}"
  retention_in_days = 14
  tags              = local.common_tags
}

locals {
  ecr_registry_uri = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${var.aws_region}.amazonaws.com"
  image_uri        = "${local.ecr_registry_uri}/policy-api-lambda:v3"
}

resource "aws_lambda_function" "api" {
  function_name = local.lambda_function_name
  role          = aws_iam_role.lambda.arn
  runtime       = "java21"
  handler       = "com.bezkoder.policylambda.PolicyApiHandler::handleRequest"
  filename      = local.lambda_package_path
  package_type  = "Zip"
  source_code_hash = filebase64sha256(local.lambda_package_path)
  memory_size      = var.lambda_memory_size
  timeout          = var.lambda_timeout_seconds
  architectures    = var.lambda_architectures

  vpc_config {
    security_group_ids = [aws_security_group.lambda.id]
    subnet_ids         = local.effective_lambda_subnet_ids
  }

  environment {
    variables = {
      DB_HOST                     = data.aws_db_instance.policy.address
      DB_PORT                     = tostring(data.aws_db_instance.policy.port)
      DB_NAME                     = var.database_name
      DB_ENCRYPT                  = "true"
      DB_TRUST_SERVER_CERTIFICATE = "true"
      DB_USERNAME                 = var.db_username
      DB_PASSWORD                 = var.db_password
      JWT_SECRET                  = var.jwt_secret
      JWT_EXPIRATION_MS           = tostring(var.jwt_expiration_ms)
      DEFAULT_ADMIN_USERNAME      = var.default_admin_username
      DEFAULT_ADMIN_EMAIL         = var.default_admin_email
      DEFAULT_ADMIN_PASSWORD      = var.default_admin_password
    }
  }

  depends_on = [
    aws_cloudwatch_log_group.lambda,
    aws_iam_role_policy_attachment.lambda_basic,
    aws_iam_role_policy_attachment.lambda_vpc_access
  ]

  tags = local.common_tags
}

resource "aws_apigatewayv2_api" "api" {
  name          = "${local.name_prefix}-api"
  protocol_type = "HTTP"
  description   = "API Gateway for Policy Explorer Lambda"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.api.id
  name        = "$default"
  auto_deploy = true
}

resource "aws_apigatewayv2_integration" "lambda" {
  api_id           = aws_apigatewayv2_api.api.id
  integration_type = "AWS_PROXY"
  integration_uri  = aws_lambda_function.api.arn
  payload_format_version = "2.0"
}

resource "aws_apigatewayv2_route" "catch_all" {
  api_id    = aws_apigatewayv2_api.api.id
  route_key = "$default"
  target    = "integrations/${aws_apigatewayv2_integration.lambda.id}"
}

resource "aws_lambda_permission" "apigateway" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.api.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.api.execution_arn}/*/*"
}
