import { ref, onUnmounted } from "vue";
import { Client } from "@stomp/stompjs";
import type { IMessage, IFrame } from "@stomp/stompjs";
import { GAME_URL } from "../config/api";

export function useGameSocket(gameId: string, playerName: string) {
  const connected = ref(false);
  const startTime = ref<Date | null>(null);
  const activeCell = ref<{ row: number; col: number } | null>(null);
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
    stomp?.subscribe(`/topic/game/${gameId}/results`, handleResults);
    stomp?.subscribe(`/topic/game/${gameId}/over`, handleGameOver);
  };

  const handleCountdown = (msg: IMessage) => {
    try {
      const data = JSON.parse(msg.body);
      console.log("COUNTDOWN message:", data);
      if (data.startTime) startTime.value = new Date(data.startTime);
    } catch {
      console.warn("Malformed countdown:", msg.body);
    }
  };

  const handleActiveCell = (msg: IMessage) => {
    try {
      const { row, col } = JSON.parse(msg.body);
      activeCell.value = { row, col };
    } catch {}
  };

  const handleResults = (msg: IMessage) => {
    try {
      const game = JSON.parse(msg.body);
      results.value = game.players;
    } catch {}
  };

  const handleGameOver = () => {
    gameOver.value = true;
    activeCell.value = null;
  };

  const sendReaction = (row: number, col: number) => {
    if (!connected.value || !stomp) return;
    stomp.publish({
      destination: "/app/reaction",
      headers: { playerName },
      body: JSON.stringify({ gameId, playerName, row, col }),
    });
  };

  onUnmounted(() => {
    stomp?.deactivate();
    stomp = null;
  });

  return {
    connect,
    connected,
    startTime,
    activeCell,
    sendReaction,
    results,
    gameOver,
  };
}
