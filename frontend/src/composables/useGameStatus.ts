import { useQuery } from "@tanstack/vue-query";
import { gameStatus } from "../api/gameApi";
import { ref } from "vue";

export function useGameStatus(sessionId: string) {
  const enabled = ref(true);

  const query = useQuery({
    queryKey: ["gameStatus", sessionId],
    queryFn: () => gameStatus(sessionId),
    refetchInterval: 1000,
    refetchIntervalInBackground: true,
    enabled,
  });

  return { ...query, stopPolling: () => (enabled.value = false) };
}
