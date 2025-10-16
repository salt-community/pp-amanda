import { useMutation } from "@tanstack/vue-query";
import { createRoom } from "../api/lobbyApi";

export function useCreateRoom() {
  return useMutation({
    mutationFn: (roomName: string) => createRoom(roomName),
    onSuccess: (data) => {
      data;
    },
  });
}
