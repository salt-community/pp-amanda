<template>
  <div class="flex flex-col items-center gap-4">
    <!-- 🎯 Game Grid -->
    <div v-if="!gameOver" class="grid grid-cols-3 gap-2">
      <div v-for="r in 3" :key="r" class="flex gap-2">
        <button
          v-for="c in 3"
          :key="`${r}-${c}`"
          @click="handleClick(r - 1, c - 1)"
          class="w-16 h-16 rounded-lg transition-all"
          :class="{
            'bg-green-500 animate-pulse':
              activeCell?.row === r - 1 && activeCell?.col === c - 1,
            'bg-gray-400': !(
              activeCell?.row === r - 1 && activeCell?.col === c - 1
            ),
          }"
        />
      </div>
    </div>

    <!-- 🏁 Game Over -->
    <div
      v-else
      class="text-3xl font-bold text-white bg-black bg-opacity-60 p-4 rounded-xl"
    >
      Game Over!
    </div>

    <!-- 🧮 Scoreboard -->
    <div v-if="results" class="mt-4 text-white">
      <h3 class="font-bold mb-2">Scores:</h3>
      <ul>
        <li v-for="(score, player) in results" :key="player">
          {{ player }}: {{ score }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useGameSocket } from "../composables/useGameSocket";

const props = defineProps<{ gameId: string; playerName: string }>();
const { connect, activeCell, sendReaction, results, gameOver } = useGameSocket(
  props.gameId,
  props.playerName
);

onMounted(() => connect());

function handleClick(row: number, col: number) {
  // Only count click if player hit the active cell
  if (activeCell.value?.row === row && activeCell.value?.col === col) {
    sendReaction(row, col);
  }
}
</script>
