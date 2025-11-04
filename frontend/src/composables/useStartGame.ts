import { useMutation } from "@tanstack/vue-query";
import { startGame } from "../api/gameApi";

export function useStartGame() {
  return useMutation({
    mutationFn: ({ gameId }: { gameId: string }) => startGame(gameId),

    onError: (err) => {
      console.error("Failed to start game:", err);
    },
  });
}
