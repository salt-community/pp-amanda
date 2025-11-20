import type {
  GameResponse,
  GameRequest,
  ResultResponse,
  TopScore,
} from "../types/game";
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

  const text = await res.text();

  if (!res.ok) {
    let message = "Failed to join game";

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
  const res = await fetch(`${GAME_URL}/game/random-name`);
  if (!res.ok) throw new Error("Failed to fetch randomized name");

  const json = await res.json();
  return json.randomName;
}

export async function getTopList(): Promise<TopScore[]> {
  const res = await fetch(`${GAME_URL}/game/top-list`);
  if (!res.ok) throw new Error("Failed to fetch toplist");

  const json = await res.json();
  return json.data;
}
