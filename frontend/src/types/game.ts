export interface GameRequest {
  gameType: "REACTION";
}

export interface GameResponse {
  gameId: string;
  sessionId: string;
  type: "REACTION";
  startTime: string | null;
  joinTimeLimit: string | null;
  endTime: string | null;
}

export interface ResultResponse {
  results: Record<string, number>;
}
