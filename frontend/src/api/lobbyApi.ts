import type { SessionResponse } from "../types/session";
import { LOBBY_URL } from "../config/api";

export async function createSession(): Promise<SessionResponse> {
  const response = await fetch(`${LOBBY_URL}/session`, {
    method: "GET",
  });

  if (!response.ok) {
    throw new Error("Failed to create session");
  }

  return await response.json();
}

export async function joinSession(sessionId: string): Promise<SessionResponse> {
  const response = await fetch(`${LOBBY_URL}/join`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ sessionId }),
  });

  if (!response.ok) {
    if (response.status === 404) {
      throw new Error("Session not found");
    }
    throw new Error("Failed to join session");
  }

  return await response.json();
}

export async function getSession(sessionId: string): Promise<SessionResponse> {
  const response = await fetch(`${LOBBY_URL}/sessions/${sessionId}`, {
    method: "GET",
  });

  if (!response.ok) {
    if (response.status === 404) {
      throw new Error("Session not found");
    }
    throw new Error("Failed to fetch session");
  }

  return await response.json();
}
