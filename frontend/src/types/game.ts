export type GameType = "REACTION";

export interface GameRequest {
  gameType: GameType;
}

export interface GameResponse {
  gameId: string;
  sessionId: string;
  type: GameType;
  startTime: string | null;
  joinTimeLimit: string | null;
  endTime: string | null;
}
