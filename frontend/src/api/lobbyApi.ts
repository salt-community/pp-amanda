import type { SessionResponse, SessionRequest } from "../types/session";
import { LOBBY_URL } from "../config/api";

export async function createRoom(
  sessionName: string
): Promise<SessionResponse> {
  const response = await fetch(`${LOBBY_URL}/create`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ sessionName } as SessionRequest),
  });

  if (!response.ok) {
    throw new Error("Failed to create room");
  }
  const data = await response.json();
  return data.sessionId;
}
