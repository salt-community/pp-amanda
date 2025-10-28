import { useMutation } from "@tanstack/vue-query";
import { initializeGame } from "../api/gameApi";
import type { GameType } from "../types/game";

export function useInitializeGame() {
  return useMutation({
    mutationFn: ({
      sessionId,
      gameType,
    }: {
      sessionId: string;
      gameType: GameType;
    }) => initializeGame(sessionId, gameType),
    onSuccess: (data) => {
      data;
    },
  });
}
