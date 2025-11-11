import { useMutation } from "@tanstack/vue-query";
import { gameResult } from "../api/gameApi";

export function useScoreBoard() {
  return useMutation({
    mutationFn: (sessionId: string) => gameResult(sessionId),
    onSuccess: (data) => {
      data;
    },
  });
}
