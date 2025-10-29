import { useMutation } from "@tanstack/vue-query";
import { selectType } from "../api/gameApi";
import type { GameType } from "../types/game";

export function useSelectGameType() {
  return useMutation({
    mutationFn: ({
      sessionId,
      gameType,
    }: {
      sessionId: string;
      gameType: GameType;
    }) => selectType(sessionId, gameType),
    onSuccess: (data) => {
      data;
    },
  });
}
