export type GameType = "REACTION";

export interface GameRequest {
  gameType: GameType;
}

export interface GameResponse {
  sessionId: string;
}
