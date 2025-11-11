import type { GameResponse, GameRequest, ResultResponse } from "../types/game";
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
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ playerName }),
  });
  if (!res.ok) throw new Error("Failed to join game");
  return await res.json();
}

export async function startGame(gameId: string) {
  const response = await fetch(`${GAME_URL}/game/${gameId}/start`, {
    method: "POST",
  });

  if (!response.ok) {
    throw new Error(`Failed to start game (status ${response.status})`);
  }
  return true;
}

export async function gameResult(gameId: string): Promise<ResultResponse> {
  const res = await fetch(`${GAME_URL}/game/${gameId}/result`);
  if (!res.ok) throw new Error("Failed to fetch game result");
  return await res.json();
}

export async function getRandomName(): Promise<string> {
  const res = await fetch(`${GAME_URL}/random-name`);
  if (!res.ok) throw new Error("Failed to fetch randomized name");
  return res.json();
}
