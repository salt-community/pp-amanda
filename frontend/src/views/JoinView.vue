<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import PlayerNameForm from "../components/PlayerNameForm.vue";
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
  <div
    class="min-h-screen flex flex-col items-center justify-start pt-60 sm:justify-center sm:pt-0 gap-6 sm:gap-10"
  >
    <Teleport to="body">
      <div
        class="fixed inset-0 bg-black/30 flex items-center justify-center z-[9999]"
      >
        <div class="quickr-panel p-8 w-[320px]">
          <PlayerNameForm :session-id="sessionId" @joined="handleJoined" />
        </div>
      </div>
    </Teleport>
  </div>
</template>
