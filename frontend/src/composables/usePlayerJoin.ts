import { useMutation } from "@tanstack/vue-query";
import { joinGame } from "../api/gameApi";

export function usePlayerJoin(sessionId: string) {
  return useMutation({
    mutationFn: (playerName: string) => joinGame(sessionId, playerName),
  });
}
