data "aws_caller_identity" "current" {}

data "aws_db_instance" "policy" {
  db_instance_identifier = var.db_instance_identifier
}

data "aws_db_subnet_group" "policy" {
  name = data.aws_db_instance.policy.db_subnet_group
}

data "aws_security_group" "rds" {
  id = local.rds_security_group_id
}

data "aws_cloudfront_cache_policy" "caching_optimized" {
  name = "Managed-CachingOptimized"
}

data "aws_cloudfront_cache_policy" "caching_disabled" {
  name = "Managed-CachingDisabled"
}

data "aws_cloudfront_origin_request_policy" "all_viewer_except_host" {
  name = "Managed-AllViewerExceptHostHeader"
}
