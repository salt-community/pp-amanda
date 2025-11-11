import { useQuery } from "@tanstack/vue-query";
import { gameResult } from "../api/gameApi";

export function useScoreBoard(gameId: string) {
  return useQuery({
    queryKey: ["scoreboard", gameId],
    queryFn: () => gameResult(gameId),
  });
}
