locals {
  name_prefix = "${var.project_name}-${var.environment}"

  frontend_bucket_name = coalesce(
    var.frontend_bucket_name,
    lower("${local.name_prefix}-${data.aws_caller_identity.current.account_id}")
  )

  lambda_function_name = "${local.name_prefix}-api"
  lambda_package_path  = abspath("${path.module}/${var.lambda_package_path}")
  static_index_path    = abspath("${path.module}/${var.static_index_path}")

  effective_lambda_subnet_ids = length(var.lambda_subnet_ids) > 0 ? var.lambda_subnet_ids : data.aws_db_subnet_group.policy.subnet_ids
  rds_security_group_id       = coalesce(var.rds_security_group_id, data.aws_db_instance.policy.vpc_security_groups[0])

  api_gateway_url = "${aws_apigatewayv2_api.api.api_endpoint}/"

  common_tags = merge(
    {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "Terraform"
    },
    var.tags
  )
}
