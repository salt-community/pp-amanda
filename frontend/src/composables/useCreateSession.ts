import { useMutation } from "@tanstack/vue-query";
import { createSession } from "../api/lobbyApi";

export function useCreateSession() {
  return useMutation({
    mutationFn: () => createSession(),
    onSuccess: (data) => {
      data;
    },
  });
}
