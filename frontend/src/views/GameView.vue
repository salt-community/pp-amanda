<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { useGameSocket } from "@/composables";
import { BackButton, ScoreBoard, GameBoard, CountdownBox } from "@/components";

const route = useRoute();
const gameId = route.params.gameId as string;
const playerName = route.query.player as string;
const showCountdown = ref(true);

const { connect, countdownSeconds, gameOver } = useGameSocket(
  gameId,
  playerName
);

onMounted(() => {
  connect();
});
</script>

<template>
  <div
    class="min-h-screen flex flex-col items-center justify-start pt-20 sm:justify-center sm:pt-0 gap-6 sm:gap-10"
  >
    <BackButton />
    <div class="flex flex-col items-center mt-10">
      <h1 class="quickr-title">Reaction Game</h1>
      <CountdownBox
        v-if="showCountdown"
        :countdown-seconds="countdownSeconds"
        @finished="showCountdown = false"
      />

      <GameBoard
        v-if="!showCountdown && !gameOver"
        :game-id="gameId"
        :player-name="playerName"
      />

      <ScoreBoard v-if="gameOver" :game-id="gameId" />
    </div>
  </div>
</template>
