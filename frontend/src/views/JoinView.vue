<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import type { GameResponse } from "@/types/game";
import { BackButton, RandomPlayerName } from "@/components";
import { useJoinDeadline } from "@/composables";
import { watch } from "vue";

const route = useRoute();
const router = useRouter();
const sessionId = route.params.sessionId as string;
const { joinClosed } = useJoinDeadline(sessionId);

watch(joinClosed, (closed) => {
  if (closed) {
    router.replace("/lobby?expired=true");
  }
});

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
  <div
    class="min-h-screen flex flex-col items-center justify-start pt-20 sm:justify-center sm:pt-0 gap-6 sm:gap-10"
  >
    <BackButton />
    <div class="flex flex-col items-center mt-10">
      <RandomPlayerName :session-id="sessionId" @joined="handleJoined" />
    </div>
  </div>
</template>
