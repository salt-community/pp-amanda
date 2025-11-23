import { useMutation } from "@tanstack/vue-query";
import { joinGame } from "@/api";

export function usePlayerJoin(sessionId: string) {
  return useMutation({
    mutationFn: (playerName: string) => joinGame(sessionId, playerName),
  });
}
