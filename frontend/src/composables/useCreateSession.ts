import { useMutation } from "@tanstack/vue-query";
import { createSession } from "../api/lobbyApi";

export function useCreateSession() {
  return useMutation({
    mutationFn: (sessionName: string) => createSession(sessionName),
    onSuccess: (data) => {
      data;
    },
  });
}
