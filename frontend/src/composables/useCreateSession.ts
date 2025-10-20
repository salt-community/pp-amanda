import { useMutation } from "@tanstack/vue-query";
import { createRoom } from "../api/lobbyApi";

export function useCreateSession() {
  return useMutation({
    mutationFn: (sessionName: string) => createRoom(sessionName),
    onSuccess: (data) => {
      data;
    },
  });
}
