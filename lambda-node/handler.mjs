import {
  DynamoDBClient,
  QueryCommand,
  PutItemCommand,
} from "@aws-sdk/client-dynamodb";
import { randomUUID } from "node:crypto";
import fetch from "node-fetch";
import "dotenv/config";

const GAME_SERVICE_URL =
  process.env.GAME_SERVICE_URL || "http://host.docker.internal:8081";

const REGION = process.env.AWS_REGION || "eu-north-1";
const ENDPOINT = process.env.LOCALSTACK_HOST
  ? "http://localstack:4566" // when running inside docker network
  : "http://localhost:4566"; // when invoked from host
const GAME_TABLE = process.env.GAME_TABLE || "Games";

const dynamodbClient = new DynamoDBClient({
  region: REGION,
  endpoint: ENDPOINT,
});

export const handler = async (event) => {
  console.log("📥 Received event from SQS or manual invoke!");
  const records = event.Records || [event];

  for (const record of records) {
    try {
      const body = record.body ? JSON.parse(record.body) : record;
      const sessionId = body.sessionId;
      console.log(`➡️ Processing sessionId: ${sessionId}`);

      // 1️⃣ Check if game already exists
      const queryRes = await dynamodbClient.send(
        new QueryCommand({
          TableName: GAME_TABLE,
          IndexName: "SessionIndex",
          KeyConditionExpression: "sessionId = :sid",
          ExpressionAttributeValues: { ":sid": { S: sessionId } },
        })
      );

      if (queryRes.Count && queryRes.Count > 0) {
        console.log(`⚠️ Game already exists for ${sessionId}`);
        continue;
      }

      // 2️⃣ Create new game
      const gameId = randomUUID();
      console.log(`✅ Creating game: ${gameId}`);
      await dynamodbClient.send(
        new PutItemCommand({
          TableName: GAME_TABLE,
          Item: {
            gameId: { S: gameId },
            sessionId: { S: sessionId },
          },
        })
      );

      // 3️⃣ Notify backend
      console.log("🚀 Calling Game service init...");
      const initRes = await fetch(`${GAME_SERVICE_URL}/game/init`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ sessionId }),
      });

      if (!initRes.ok) throw new Error(`Init failed: ${initRes.status}`);
      console.log("✅ Game init successful");
    } catch (err) {
      console.error("❌ Error processing record:", err);
    }
  }
};
