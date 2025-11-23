import type { SessionResponse } from "../types/session";
import { LOBBY_URL } from "../config/api";

export async function createSession(): Promise<SessionResponse> {
  const response = await fetch(`${LOBBY_URL}/lobby/session`, {
    method: "GET",
  });

  if (!response.ok) {
    throw new Error("Failed to create session");
  }

  return await response.json();
}

export async function joinSession(sessionId: string): Promise<SessionResponse> {
  const response = await fetch(`${LOBBY_URL}/lobby/join`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ sessionId }),
  });

  const text = await response.text();

  if (!response.ok) {
    let message = "Failed to join session";

    try {
      const json = JSON.parse(text);
      message = json.error || json.message || message;
    } catch {
      message = text || message;
    }

    throw new Error(message);
  }

  return JSON.parse(text);
}

export async function getSession(sessionId: string): Promise<SessionResponse> {
  const response = await fetch(`${LOBBY_URL}/lobby/sessions/${sessionId}`, {
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
