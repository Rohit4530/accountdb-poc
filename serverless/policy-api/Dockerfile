FROM public.ecr.aws/lambda/java:21

# Copy the shaded jar to the Lambda task root
COPY target/policy-api-lambda.jar ${LAMBDA_TASK_ROOT}/lib/

# Set the handler (for container images, use just the class name)
CMD ["com.bezkoder.policylambda.PolicyApiHandler"]
