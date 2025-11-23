import { joinSession } from "@/api";
import { useMutation } from "@tanstack/vue-query";

export function useJoinSession() {
  return useMutation({
    mutationFn: (sessionId: string) => joinSession(sessionId),
  });
}
