#!/bin/bash
set -e

ENDPOINT="http://localstack:4566"
REGION="eu-north-1"
LAMBDA_NAME="session-created-handler"
ROLE_ARN="arn:aws:iam::000000000000:role/dummy-role"
ZIP_PATH="lambda-node.zip"
HANDLER="handler.handler"
QUEUE_NAME="session-created-queue"

# Load .env file from lambda-node
set -a
source ../lambda-node/.env
set +a

echo " ➡️ Pack and zip Lambda"
cd ../lambda-node
zip -r ../scripts/lambda-node.zip . >/dev/null
cd ../scripts

echo " ➡️ Deploy Lambda"
if aws --endpoint-url=$ENDPOINT lambda get-function --function-name $LAMBDA_NAME >/dev/null 2>&1; then
  aws --endpoint-url=$ENDPOINT lambda update-function-code \
    --function-name $LAMBDA_NAME \
    --zip-file fileb://$ZIP_PATH >/dev/null
  echo " ➡️ Lambda updated"
else
  aws --endpoint-url=$ENDPOINT lambda create-function \
    --function-name $LAMBDA_NAME \
    --runtime nodejs20.x \
    --handler $HANDLER \
    --role $ROLE_ARN \
    --timeout 15 \
    --zip-file fileb://$ZIP_PATH >/dev/null
  echo " ➡️ Lambda created"
fi

echo " ➡️ Connect SQS to Lambda..."
aws --endpoint-url=$ENDPOINT lambda create-event-source-mapping \
  --function-name $LAMBDA_NAME \
  --batch-size 1 \
  --event-source-arn arn:aws:sqs:$REGION:000000000000:$QUEUE_NAME >/dev/null 2>&1 || true

echo " ➡️ Waiting for Lambda to become active..."
aws --endpoint-url=$ENDPOINT lambda wait function-active --function-name $LAMBDA_NAME

# 👇 Add environment variables from .env
echo " ➡️ Updating Lambda environment variables..."
aws --endpoint-url=$ENDPOINT lambda update-function-configuration \
  --function-name $LAMBDA_NAME \
  --environment "Variables={GAME_SERVICE_URL=${GAME_SERVICE_URL},LOCALSTACK_HOST=${LOCALSTACK_HOST},GAME_TABLE=${GAME_TABLE}}" >/dev/null

echo " ✅ Lambda deployed locally with environment vars!"
