output "cloudfront_domain_name" {
  description = "CloudFront domain that serves the frontend and proxies API calls."
  value       = aws_cloudfront_distribution.app.domain_name
}

output "cloudfront_url" {
  description = "Convenient HTTPS URL for the deployed application."
  value       = "https://${aws_cloudfront_distribution.app.domain_name}"
}

output "lambda_function_name" {
  description = "Lambda function name for the policy API."
  value       = aws_lambda_function.api.function_name
}

output "lambda_function_url" {
  description = "API Gateway URL for the policy API."
  value       = aws_apigatewayv2_api.api.api_endpoint
}

output "frontend_bucket_name" {
  description = "S3 bucket hosting the static frontend."
  value       = aws_s3_bucket.frontend.bucket
}

output "rds_endpoint" {
  description = "Referenced RDS instance endpoint."
  value       = data.aws_db_instance.policy.address
}

output "rds_port" {
  description = "Referenced RDS instance port."
  value       = data.aws_db_instance.policy.port
}
