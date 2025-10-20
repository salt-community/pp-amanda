import { useMutation } from "@tanstack/vue-query";
import { joinSession } from "../api/lobbyApi";

export function useJoinSession() {
  return useMutation({
    mutationFn: (sessionId: string) => joinSession(sessionId),
    onSuccess: (data) => {
      data;
    },
  });
}
