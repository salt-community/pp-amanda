import { ref, onUnmounted } from "vue";
import { Client, type IMessage, type IFrame } from "@stomp/stompjs";
import { GAME_URL } from "../config/api";

export function useStomp(gameId: string) {
  const connected = ref(false);
  const messages = ref<any[]>([]);
  let stompClient: Client | null = null;

  const connect = () => {
    if (stompClient) return;

    const wsUrl = `${GAME_URL.replace("http", "ws")}/ws-game`;

    stompClient = new Client({
      webSocketFactory: () => new WebSocket(wsUrl),
      reconnectDelay: 5000,
      onConnect,
      onDisconnect,
      onStompError: onError,
    });

    stompClient.activate();
  };

  const onConnect = (_frame: IFrame) => {
    connected.value = true;
    stompClient?.subscribe(`/topic/game/${gameId}/countdown`, handleCountdown);
    stompClient?.subscribe(`/topic/game/${gameId}/events`, handleEvent);
  };

  const handleCountdown = (msg: IMessage) => {
    try {
      const data = JSON.parse(msg.body);
      messages.value.push(data);
    } catch {
      /* ignore malformed messages */
    }
  };

  const handleEvent = (msg: IMessage) => {
    try {
      JSON.parse(msg.body);
    } catch {
      /* ignore malformed messages */
    }
  };

  const onDisconnect = () => {
    connected.value = false;
  };

  const onError = (_frame: IFrame) => {
    connected.value = false;
  };

  const send = (destination: string, body: object) => {
    if (!stompClient || !connected.value) return;
    stompClient.publish({ destination, body: JSON.stringify(body) });
  };

  onUnmounted(() => {
    stompClient?.deactivate();
    stompClient = null;
  });

  return { connect, connected, messages, send };
}
