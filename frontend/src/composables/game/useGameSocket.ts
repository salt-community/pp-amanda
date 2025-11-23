import { ref, onUnmounted } from "vue";
import { Client } from "@stomp/stompjs";
import type { IMessage, IFrame } from "@stomp/stompjs";
import { GAME_URL } from "@/config/api";

export function useGameSocket(gameId: string, playerName: string) {
  const connected = ref(false);
  const countdownSeconds = ref<number | null>(null);
  const liveScores = ref<Record<string, number> | null>(null);

  const activeCells = ref<
    Array<{ row: number; col: number; activatedAt: number; expiresAt: number }>
  >([]);
  const currentRound = ref<number>(0);
  const results = ref<Record<string, number> | null>(null);
  const gameOver = ref(false);

  let stomp: Client | null = null;

  const connect = () => {
    if (stomp) return;
    const wsUrl = `${GAME_URL.replace("http", "ws")}/ws-game`;

    stomp = new Client({
      webSocketFactory: () => new WebSocket(wsUrl),
      reconnectDelay: 3000,
      onConnect,
      onStompError: console.error,
      onDisconnect: () => (connected.value = false),
    });

    stomp.activate();
  };

  const onConnect = (_frame: IFrame) => {
    connected.value = true;
    stomp?.subscribe(`/topic/game/${gameId}/countdown`, handleCountdown);
    stomp?.subscribe(`/topic/game/${gameId}`, handleActiveCell);
    stomp?.subscribe(`/topic/game/${gameId}/scores`, handleResults);
    stomp?.subscribe(`/topic/game/${gameId}/over`, handleGameOver);
  };

  const handleCountdown = (msg: IMessage) => {
    try {
      const data = JSON.parse(msg.body);
      if (data.eventType === "COUNTDOWN_STARTED") {
        countdownSeconds.value = data.seconds ?? 5;
      }
    } catch {
      console.warn("Malformed countdown:", msg.body);
    }
  };

  const handleActiveCell = (msg: IMessage) => {
    try {
      const data = JSON.parse(msg.body);
      activeCells.value = data.cells || [];

      if (data.scores) {
        liveScores.value = data.scores;
      }

      currentRound.value++;
    } catch {
      console.warn("Malformed activeCell:", msg.body);
    }
  };

  const handleResults = (msg: IMessage) => {
    try {
      const game = JSON.parse(msg.body);
      results.value = game.players;
    } catch {}
  };

  const handleGameOver = () => {
    gameOver.value = true;
    activeCells.value = [];
  };

  const sendReaction = (row: number, col: number, activatedAt: number) => {
    if (!connected.value || !stomp) return;
    const reactionTimestamp = Date.now();
    stomp.publish({
      destination: "/app/reaction",
      headers: { playerName },
      body: JSON.stringify({
        gameId,
        playerName,
        row,
        col,
        activatedAt,
        reactionTimestamp,
      }),
    });
  };

  onUnmounted(() => {
    stomp?.deactivate();
    stomp = null;
  });

  return {
    connect,
    connected,
    countdownSeconds,
    activeCells,
    currentRound,
    liveScores,
    sendReaction,
    results,
    gameOver,
  };
}
