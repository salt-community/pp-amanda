<template>
  <div>
    <InitializeGame
      v-if="!gameType"
      :session-id="sessionId"
      @initialized="handleInitialized"
    />
    <GameBoard v-else :game-type="gameType" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import InitializeGame from "../components/InitializeGame.vue";
import GameBoard from "../components/GameBoard.vue";
import type { GameType } from "../types/game";
import { useRoute } from "vue-router";

const route = useRoute();
const sessionId = route.params.sessionId as string;

const gameType = ref<GameType | null>(null);
function handleInitialized(type: GameType) {
  gameType.value = type;
}
</script>
