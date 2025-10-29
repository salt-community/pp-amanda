import type { GameResponse, GameRequest } from "../types/game";
import { GAME_URL } from "../config/api";
import type { GameType } from "../types/game";

export async function selectType(
  sessionId: string,
  gameType: GameType
): Promise<GameResponse> {
  const response = await fetch(`${GAME_URL}/${sessionId}/initialize`, {
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
  const res = await fetch(`${GAME_URL}/${sessionId}/status`);
  if (!res.ok) throw new Error("Failed to fetch game status");
  return await res.json();
}
