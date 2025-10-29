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
import { ref } from "vue";
import SelectGameType from "../components/SelectGameType.vue";
import GameBoard from "../components/GameBoard.vue";
import type { GameType } from "../types/game";
import { useRoute } from "vue-router";

const route = useRoute();
const sessionId = route.params.sessionId as string;
const gameType = ref<GameType | null>(null);

function handleSelect(type: GameType) {
  gameType.value = type;
}
</script>
