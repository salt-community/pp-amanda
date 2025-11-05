<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import PlayerNameForm from "../components/PlayerNameForm.vue";
import type { GameResponse } from "../types/game";

const route = useRoute();
const router = useRouter();
const sessionId = route.params.sessionId as string;

function handleJoined(gameResponse: GameResponse) {
  const { gameId } = gameResponse;
  router.push(`/game/${gameId}`);
}
</script>

<template>
  <div>
    <Teleport to="body">
      <div
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
