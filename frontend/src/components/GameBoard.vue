<template>
  <div
    class="min-h-screen bg-gradient-to-b from-slate-900 to-slate-800 text-white flex flex-col items-center justify-center gap-6"
  >
    <h1 class="text-4xl font-extrabold tracking-wide">⚡ Reaction Game ⚡</h1>
    <div class="text-sm text-gray-400">Active: {{ activeCell }}</div>

    <!-- ✅ Single grid -->
    <div
      v-if="!gameOver"
      class="grid gap-3"
      :style="{ gridTemplateColumns: `repeat(${gridSize}, 6rem)` }"
    >
      <button
        v-for="(r, index) in gridCells"
        :key="`${r.row}-${r.col}`"
        @click="handleClick(r.row, r.col)"
        class="w-20 h-20 rounded-xl border border-gray-700 shadow-inner transition-all duration-200 ease-in-out select-none"
        :class="[
          'w-24 h-24 rounded-xl border border-gray-700 shadow-inner transition-all duration-300 ease-in-out select-none',
          activeCell?.row === r.row && activeCell?.col === r.col
            ? 'bg-lime-400 scale-110 shadow-lg ring-4 ring-lime-500 animate-pulse'
            : 'bg-gray-800 hover:bg-gray-700 text-white active:scale-95',
        ]"
      ></button>
    </div>

    <!-- 🏁 Game Over -->
    <div v-else class="text-5xl font-extrabold text-red-400 animate-bounce">
      Game Over!
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useGameSocket } from "../composables/useGameSocket";

const props = defineProps<{ gameId: string; playerName: string }>();
const { connect, activeCell, sendReaction, gameOver } = useGameSocket(
  props.gameId,
  props.playerName
);

const gridSize = 5;
const gridCells = computed(() =>
  Array.from({ length: gridSize * gridSize }, (_, i) => ({
    row: Math.floor(i / gridSize),
    col: i % gridSize,
  }))
);

function isActive(row: number, col: number) {
  return activeCell.value?.row === row && activeCell.value?.col === col;
}

function handleClick(row: number, col: number) {
  if (isActive(row, col)) sendReaction(row, col);
}

onMounted(() => connect());
</script>
