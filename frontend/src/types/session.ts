export interface SessionResponse {
  sessionId: string;
}

export interface SessionResponse {
  sessionId: string;
  sessionName: string;
  createdAt: number;
}

export interface SessionRequest {
  sessionName: string;
}

export interface JoinSessionRequest {
  sessionId: string;
}
