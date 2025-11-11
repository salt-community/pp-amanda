<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import CountdownBox from "../components/CountdownBox.vue";
import GameBoard from "../components/GameBoard.vue";
import { useGameSocket } from "../composables/useGameSocket";
import ScoreBoard from "../components/ScoreBoard.vue";

const route = useRoute();
const gameId = route.params.gameId as string;
const playerName = route.query.player as string;
const showCountdown = ref(true);

const { connect, connected, countdownSeconds, gameOver } = useGameSocket(
  gameId,
  playerName
);

onMounted(() => {
  connect();
});
</script>

<template>
  <div class="p-4 text-center">
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
