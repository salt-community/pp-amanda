<template>
  <div>
    <Teleport to="body">
      <div
        v-if="isFetching"
        style="
          position: fixed;
          inset: 0;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 1.2rem;
          z-index: 10000;
        "
      >
        Loading ...
      </div>
      <div
        v-else-if="!gameType"
        style="
          position: fixed;
          inset: 0;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 9999;
        "
      >
        <div
          style="
            background: white;
            padding: 20px;
            border-radius: 8px;
            min-width: 300px;
          "
        >
          <SelectGameType :session-id="sessionId" @initialized="handleSelect" />
        </div>
      </div>
      <div
        v-else
        style="
          position: fixed;
          inset: 0;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 9999;
        "
      >
        <div
          style="
            background: white;
            padding: 20px;
            border-radius: 8px;
            min-width: 300px;
          "
        >
          <PlayerNameForm :session-id="sessionId" @joined="handleJoined" />
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useGameStatus } from "../composables/useGameStatus.ts";
import { useStartGame } from "../composables/useStartGame.ts";
import SelectGameType from "../components/SelectGameType.vue";
import PlayerNameForm from "../components/PlayerNameForm.vue";
import type { GameResponse } from "../types/game";

const route = useRoute();
const router = useRouter();
const sessionId = route.params.sessionId as string;

const { data, stopPolling, isFetching } = useGameStatus(sessionId);

const localType = ref<string | null>(null);
const gameType = computed<string | null>(
  () => localType.value ?? data.value?.gameType ?? null
);

const joinClosed = computed(() => {
  if (!data.value?.joinDeadline) return false;
  const deadline = new Date(data.value.joinDeadline).getTime();
  return Date.now() > deadline;
});

onMounted(() => {
  if (data.value?.gameType) {
    localType.value = data.value.gameType;
  }
});

watch(
  () => joinClosed.value,
  (closed) => {
    if (closed && data.value?.gameId) {
      stopPolling();
      router.push(`/game/${data.value.gameId}`);
    }
  }
);

function handleSelect(type: string) {
  localType.value = type;
  stopPolling();
}

const { mutate: startGameMutation } = useStartGame();

function handleJoined(gameResponse: GameResponse) {
  const { gameId } = gameResponse;
  startGameMutation({ gameId });
  router.push(`/game/${gameId}`);
}
</script>
