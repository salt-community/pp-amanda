import { useMutation } from "@tanstack/vue-query";
import { initQuickr } from "../api/gameApi";

export function useInitQuickr() {
  return useMutation({
    mutationFn: ({
      sessionId,
      gameType,
    }: {
      sessionId: string;
      gameType: string;
    }) => initQuickr(sessionId, gameType),
    onSuccess: (data) => {
      data;
    },
  });
}
