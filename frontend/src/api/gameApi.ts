import type { GameResponse, GameRequest } from "../types/game";
import { GAME_URL } from "../config/api";

export async function initQuickr(
  sessionId: string,
  gameType: string
): Promise<GameResponse> {
  const response = await fetch(`${GAME_URL}/game/${sessionId}/type`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ gameType } as GameRequest),
  });

  if (!response.ok) {
    throw new Error("Failed to create session");
  }

  return await response.json();
}

export async function gameStatus(sessionId: string) {
  const res = await fetch(`${GAME_URL}/game/${sessionId}/status`);
  if (!res.ok) throw new Error("Failed to fetch game status");
  return await res.json();
}

export async function joinGame(sessionId: string, playerName: string) {
  const res = await fetch(`${GAME_URL}/game/${sessionId}/join`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ playerName }),
  });
  if (!res.ok) throw new Error("Failed to join game");
  return await res.json();
}
