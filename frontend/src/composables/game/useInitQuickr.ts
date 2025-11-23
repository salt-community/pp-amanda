import { initQuickr } from "@/api";
import { useMutation } from "@tanstack/vue-query";

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
