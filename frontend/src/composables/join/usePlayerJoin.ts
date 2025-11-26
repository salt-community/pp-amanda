import { useMutation } from "@tanstack/vue-query";
import { joinGame } from "@/api";

export function usePlayerJoin(getSessionId: () => string) {
  return useMutation({
    mutationFn: (playerName: string) => joinGame(getSessionId(), playerName),
  });
}
