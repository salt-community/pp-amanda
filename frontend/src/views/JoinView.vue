<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import RandomizePlayerName from "../components/RandomizePlayerName.vue";
import type { GameResponse } from "../types/game";

const route = useRoute();
const router = useRouter();
const sessionId = route.params.sessionId as string;

function handleJoined({
  gameResponse,
  playerName,
}: {
  gameResponse: GameResponse;
  playerName: string;
}) {
  const { gameId } = gameResponse;
  router.push({
    path: `/game/${gameId}`,
    query: { player: playerName },
  });
}
</script>

<template>
  <div class="min-h-screen flex bg-black/30 items-center justify-center">
    <RandomizePlayerName :session-id="sessionId" @joined="handleJoined" />
  </div>
</template>
