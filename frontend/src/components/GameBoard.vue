<template>
  <div
    class="min-h-screen flex flex-col items-center justify-center gap-8 px-4"
  >
    <h1
      class="text-3xl sm:text-4xl md:text-5xl text-amber-500 font-bold tracking-widest uppercase"
    >
      Reaction Game
    </h1>
    <div v-if="liveScores" class="mt-6">
      <ul>
        <li v-for="(score, player) in liveScores" :key="player">
          {{ player }}: {{ score }}
        </li>
      </ul>
    </div>

    <div
      v-if="!gameOver"
      class="border-8 border-double border-amber-700 rounded-md p-8 flex flex-col items-center justify-center"
    >
      <div
        class="grid gap-4 place-items-center"
        :style="{
          gridTemplateColumns: `repeat(${gridSize}, minmax(3rem, 5rem))`,
        }"
      >
        <button
          v-for="cell in gridCells"
          :key="`${cell.row}-${cell.col}`"
          @click="handleClick(cell.row, cell.col)"
          class="text-5xl sm:text-6xl select-none transition-all duration-50"
          :class="[
            activeCell?.row === cell.row && activeCell?.col === cell.col
              ? 'scale-100 animate-pulse drop-shadow-[0_0_15px_rgba(255,191,0,0.7)]'
              : 'scale-50 opacity-40 cursor-not-allowed drop-shadow-none',
          ]"
        >
          {{
            activeCell?.row === cell.row && activeCell?.col === cell.col
              ? "🟠"
              : "⚫️"
          }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useGameSocket } from "../composables/useGameSocket";

const props = defineProps<{
  gameId: string;
  playerName: string;
}>();

const { connect, activeCell, sendReaction, gameOver, liveScores } =
  useGameSocket(props.gameId, props.playerName);

const gridSize = 4;
const gridCells = computed(() =>
  Array.from({ length: gridSize * gridSize }, (_, i) => ({
    row: Math.floor(i / gridSize),
    col: i % gridSize,
  }))
);

function handleClick(row: number, col: number) {
  if (activeCell.value?.row === row && activeCell.value?.col === col) {
    sendReaction(row, col);
  }
}

onMounted(() => connect());
</script>
