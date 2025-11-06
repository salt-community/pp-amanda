<script setup lang="ts">
import { onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { useStomp } from "../composables/useStomp";
import CountdownBox from "../components/CountdownBox.vue";
import GameBoard from "../components/GameBoard.vue";
import { useGameSocket } from "../composables/useGameSocket";

const route = useRoute();
const gameId = route.params.gameId as string;
const playerName = route.query.player as string;

const { connect, connected, messages } = useStomp(gameId);
const { startTime } = useGameSocket(gameId, playerName);

onMounted(() => {
  connect();
});

watch(
  () => messages.value.length,
  () => {
    const latest = messages.value[messages.value.length - 1];
    if (latest?.startTime) {
      startTime.value = new Date(latest.startTime);
    }
  }
);
</script>

<template>
  <div class="p-4 text-center">
    <p>Status: {{ connected ? "Connected" : "Disconnected" }}</p>
    <div class="flex flex-col items-center mt-10">
      <h1 class="text-2xl font-bold mb-4">Reaction Game</h1>
      <CountdownBox :start-time="startTime" />
      <GameBoard v-if="startTime" :game-id="gameId" player-name="playerName" />
    </div>
  </div>
</template>
