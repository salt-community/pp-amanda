<template>
  <div>
    <Teleport to="body">
      <div
        v-if="!gameType"
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
import { ref, computed, watchEffect } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useGameStatus } from "../composables/useGameStatus.ts";
import { useStartGame } from "../composables/useStartGame.ts";
import SelectGameType from "../components/SelectGameType.vue";
import PlayerNameForm from "../components/PlayerNameForm.vue";
import type { GameResponse } from "../types/game";

const route = useRoute();
const router = useRouter();
const sessionId = route.params.sessionId as string;

const { data, stopPolling } = useGameStatus(sessionId);

const localType = ref<String | null>(null);
const gameType = computed<String | null>(
  () => localType.value ?? data.value?.gameType ?? null
);

// time limitation for joining
const joinClosed = computed(() => {
  if (!data.value?.joinDeadline) return false;
  const deadline = new Date(data.value.joinDeadline).getTime();
  return Date.now() > deadline;
});

// if someone else in this session set the gametype
watchEffect(() => {
  if (joinClosed.value && data.value?.gameId) {
    stopPolling();
    router.push(`/game/${data.value.gameId}`);
  }
});

//
function handleSelect(type: String) {
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
