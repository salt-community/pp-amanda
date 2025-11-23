import { gameResult } from "@/api";
import { useQuery } from "@tanstack/vue-query";

export function useScoreBoard(gameId: string) {
  return useQuery({
    queryKey: ["scoreboard", gameId],
    queryFn: () => gameResult(gameId),
  });
}
