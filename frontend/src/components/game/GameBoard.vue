<template>
  <h1
    class="text-3xl sm:text-4xl md:text-5xl text-amber-500 font-bold tracking-widest uppercase"
  >
    Quickr
  </h1>
  <div v-if="topScores" class="mt-6 min-h-[5rem]">
    <ul>
      <li v-for="[name, score] in topScores" :key="name">
        {{ name }}: {{ score }}
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
          isActive(cell)
            ? 'scale-100 animate-pulse drop-shadow-[0_0_15px_rgba(255,191,0,0.7)]'
            : 'scale-50 opacity-40 cursor-not-allowed drop-shadow-none',
        ]"
      >
        {{ isActive(cell) ? "🟠" : "⚫️" }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGameSocket } from "@/composables";
import { computed, onMounted } from "vue";

const props = defineProps<{
  gameId: string;
  playerName: string;
}>();

const { connect, activeCells, sendReaction, gameOver, liveScores } =
  useGameSocket(props.gameId, props.playerName);

const gridSize = 4;
const gridCells = computed(() =>
  Array.from({ length: gridSize * gridSize }, (_, i) => ({
    row: Math.floor(i / gridSize),
    col: i % gridSize,
  }))
);

function isActive(cell: { row: number; col: number }) {
  return activeCells.value.some(
    (c) => c.row === cell.row && c.col === cell.col
  );
}

function handleClick(row: number, col: number) {
  const match = activeCells.value.find((c) => c.row === row && c.col === col);

  if (match) {
    sendReaction(row, col, match.activatedAt);
  }
}

const topScores = computed(() => {
  if (!liveScores.value) return [];

  return Object.entries(liveScores.value)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 3);
});

onMounted(() => connect());
</script>
