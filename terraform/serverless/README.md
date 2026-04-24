# Serverless Policy Explorer

This Terraform stack creates the AWS pieces needed to run the policy explorer as a serverless application:

- `S3` for the static frontend
- `CloudFront` for a single public URL
- `Lambda` for the auth and policy APIs
- security group wiring so the Lambda function can connect to an existing `RDS SQL Server` instance

It deliberately does **not** create the RDS instance. The database is referenced through Terraform `data` blocks.

## What the Lambda application does

The backend code lives in [serverless/policy-api](../../serverless/policy-api/pom.xml). On cold start it:

- creates the `users`, `roles`, and `user_roles` tables if they do not exist
- ensures `ROLE_USER`, `ROLE_MODERATOR`, and `ROLE_ADMIN` exist
- ensures one default admin user exists and is mapped to `ROLE_ADMIN`

The API surface matches the current UI flow:

- `POST /api/auth/signup`
- `POST /api/auth/signin`
- `GET /api/test/all`
- `GET /api/policies/policy-types`
- `GET /api/policies/policy-statuses?policyType=...`
- `GET /api/policies/search?policyType=...&policyStatus=...`
- `GET /api/policies/{id}`

## Build The Lambda Jar

From the repo root:

```powershell
$env:JAVA_HOME='C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.4.1\jbr'
.\mvnw -f serverless\policy-api\pom.xml -DskipTests package
```

That creates:

- `serverless/policy-api/target/policy-api-lambda.jar`
- `serverless/policy-api/target/policy-api-lambda.zip`

Terraform uploads the generated zip file directly to Lambda.

## Deploy

1. Copy [terraform.tfvars.example](./terraform.tfvars) to `terraform.tfvars`.
2. Fill in the DB credentials, JWT secret, and default admin password.
3. Run Terraform:

```powershell
cd terraform\serverless
terraform init
terraform plan
terraform apply
```

After apply, Terraform outputs the CloudFront URL for the app.

## Important Notes

- The module reads the existing RDS instance with `data "aws_db_instance"`.
- By default it uses the first security group attached to that DB instance. If your DB uses multiple security groups, set `rds_security_group_id` explicitly.
- By default the Lambda function uses the subnet IDs from the RDS subnet group. Override `lambda_subnet_ids` only if you need a narrower set.
- Sensitive values like DB passwords and default admin passwords end up in Terraform state. Use an encrypted remote backend for real environments.
