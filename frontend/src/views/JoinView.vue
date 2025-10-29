<template>
  <div>
    <GameBoard v-if="gameType" :game-type="gameType" />

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
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watchEffect } from "vue";
import { useRoute } from "vue-router";
import { useGameStatus } from "../composables/useGameStatus.ts";
import SelectGameType from "../components/SelectGameType.vue";
import GameBoard from "../components/GameBoard.vue";
import type { GameType } from "../types/game.ts";

const route = useRoute();
const sessionId = route.params.sessionId as string;

const { data, stopPolling } = useGameStatus(sessionId);

const localType = ref<GameType | null>(null);

const gameType = computed<GameType | null>(
  () => localType.value ?? data.value?.gameType ?? null
);

watchEffect(() => {
  if (data.value?.gameType) stopPolling();
});

function handleSelect(type: GameType) {
  localType.value = type;
  stopPolling();
}
</script>
