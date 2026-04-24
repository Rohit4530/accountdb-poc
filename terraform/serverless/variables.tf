variable "aws_region" {
  description = "AWS region where the serverless stack will be created."
  type        = string
  default     = "ap-south-1"
}

variable "project_name" {
  description = "Short project name used for resource naming."
  type        = string
  default     = "policy-explorer"
}

variable "environment" {
  description = "Deployment environment label."
  type        = string
  default     = "dev"
}

variable "db_instance_identifier" {
  description = "Existing RDS SQL Server instance identifier. This stack references it through a data source and does not create it."
  type        = string
}

variable "database_name" {
  description = "Database name inside the existing SQL Server instance."
  type        = string
  default     = "customerDB"
}

variable "db_username" {
  description = "Database username used by the Lambda API."
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Database password used by the Lambda API."
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT signing secret for the Lambda API. Use at least 32 characters."
  type        = string
  sensitive   = true

  validation {
    condition     = length(var.jwt_secret) >= 32
    error_message = "jwt_secret must be at least 32 characters long."
  }
}

variable "jwt_expiration_ms" {
  description = "JWT access token lifetime in milliseconds."
  type        = number
  default     = 86400000
}

variable "default_admin_username" {
  description = "Default admin username seeded by the Lambda application on startup."
  type        = string
  default     = "policyadmin"
}

variable "default_admin_email" {
  description = "Default admin email seeded by the Lambda application on startup."
  type        = string
  default     = "policyadmin@example.com"
}

variable "default_admin_password" {
  description = "Default admin password seeded by the Lambda application on startup."
  type        = string
  sensitive   = true
}

variable "lambda_timeout_seconds" {
  description = "Lambda timeout in seconds."
  type        = number
  default     = 30
}

variable "lambda_memory_size" {
  description = "Lambda memory size in MB."
  type        = number
  default     = 1024
}

variable "lambda_architectures" {
  description = "Lambda CPU architecture list."
  type        = list(string)
  default     = ["x86_64"]
}

variable "lambda_package_path" {
  description = "Relative path from this Terraform module to the zipped Lambda deployment package."
  type        = string
  default     = "../../serverless/policy-api/target/policy-api-lambda.jar"
}

variable "static_index_path" {
  description = "Relative path from this Terraform module to the frontend HTML file that should be uploaded to S3."
  type        = string
  default     = "../../src/main/resources/static/index.html"
}

variable "frontend_bucket_name" {
  description = "Optional S3 bucket name for the frontend. Leave null to derive one from the project name and AWS account ID."
  type        = string
  default     = null
}

variable "force_destroy_frontend_bucket" {
  description = "Whether Terraform can delete a non-empty frontend bucket."
  type        = bool
  default     = false
}

variable "cloudfront_price_class" {
  description = "CloudFront price class for the distribution."
  type        = string
  default     = "PriceClass_100"
}

variable "lambda_subnet_ids" {
  description = "Optional subnet IDs for the Lambda VPC config. Leave empty to reuse the subnets from the RDS subnet group."
  type        = list(string)
  default     = []
}

variable "rds_security_group_id" {
  description = "Optional RDS security group override. Leave null to use the first security group attached to the DB instance."
  type        = string
  default     = null
}

variable "tags" {
  description = "Additional tags applied to created resources."
  type        = map(string)
  default     = {}
}
