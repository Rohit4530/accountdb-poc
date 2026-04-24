resource "aws_s3_bucket" "frontend" {
  bucket        = local.frontend_bucket_name
  force_destroy = var.force_destroy_frontend_bucket
  tags          = local.common_tags
}

resource "aws_s3_bucket_ownership_controls" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "frontend" {
  bucket                  = aws_s3_bucket.frontend.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_object" "index" {
  bucket        = aws_s3_bucket.frontend.id
  key           = "index.html"
  content       = replace(file(local.static_index_path), "CLOUDFRONT_URL_PLACEHOLDER", "https://${aws_cloudfront_distribution.app.domain_name}")
  content_type  = "text/html; charset=utf-8"
  cache_control = "no-cache, max-age=0"

  depends_on = [
    aws_s3_bucket_ownership_controls.frontend
  ]
}
