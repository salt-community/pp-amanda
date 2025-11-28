#!/bin/bash
set -e

ENDPOINT="http://localstack:4566"
REGION="eu-north-1"
QUEUE_NAME="session-created-queue"

echo " ➡️ Creating SQS queue..."
aws --endpoint-url=$ENDPOINT sqs create-queue --queue-name $QUEUE_NAME >/dev/null 2>&1 || true

echo "✓ Creating DynamoDB table Sessions..."
aws --endpoint-url=$ENDPOINT dynamodb create-table \
  --table-name Sessions \
  --attribute-definitions AttributeName=sessionId,AttributeType=S \
  --key-schema AttributeName=sessionId,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 >/dev/null 2>&1 || echo "ℹ️ Sessions table exists"

echo " ➡️ Creating DynamoDB table Games..."
aws --endpoint-url=$ENDPOINT dynamodb create-table \
  --table-name Games \
  --attribute-definitions AttributeName=gameId,AttributeType=S AttributeName=sessionId,AttributeType=S \
  --key-schema AttributeName=gameId,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --global-secondary-indexes '[
    {
      "IndexName": "SessionIndex",
      "KeySchema": [{"AttributeName":"sessionId","KeyType":"HASH"}],
      "Projection": {"ProjectionType":"ALL"},
      "ProvisionedThroughput": {"ReadCapacityUnits":5,"WriteCapacityUnits":5}
    }
  ]' >/dev/null 2>&1 || echo " ℹ️ Games table exists"

  echo " ➡️ Creating DynamoDB table TopScores..."
aws --endpoint-url=$ENDPOINT dynamodb create-table \
  --table-name TopScores \
  --attribute-definitions \
      AttributeName=pk,AttributeType=S \
      AttributeName=sk,AttributeType=S \
  --key-schema \
      AttributeName=pk,KeyType=HASH \
      AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST >/dev/null 2>&1 || echo " ℹ️ TopScores table exists"

echo " ✅ LocalStack environment ready!"