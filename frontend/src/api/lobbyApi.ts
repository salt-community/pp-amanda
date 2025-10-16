import type { RoomIdResponse, RoomRequest } from "../types/lobby";
import { LOBBY_URL } from "../config/api";

export async function createRoom(roomName: string): Promise<RoomIdResponse> {
  const response = await fetch(`${LOBBY_URL}/create`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ roomName } as RoomRequest),
  });

  if (!response.ok) {
    throw new Error("Failed to create room");
  }
  const data = await response.json();
  return data.roomId;
}
