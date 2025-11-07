<template>
  <div
    class="min-h-screen bg-gradient-to-b from-slate-900 to-slate-800 text-white flex flex-col items-center justify-center gap-6"
  >
    <h1 class="text-4xl font-extrabold tracking-wide">⚡ Reaction Game ⚡</h1>

    <div class="text-sm text-gray-400">Active: {{ activeCell }}</div>

    <div
      v-if="!gameOver"
      class="grid gap-3"
      :style="{ gridTemplateColumns: `repeat(${rows.length}, minmax(0, 1fr))` }"
    >
      <template v-for="r in rows" :key="r">
        <template v-for="c in cols" :key="`${r}-${c}`">
          <button
            @click="handleClick(r, c)"
            class="w-24 h-24 rounded-xl border border-gray-700 font-bold text-xl flex items-center justify-center shadow-inner transition-all duration-200 ease-in-out select-none"
            :class="
              isActive(r, c)
                ? 'bg-lime-400 scale-110 shadow-lg ring-4 ring-lime-500 animate-pulse'
                : 'bg-gray-800 hover:bg-gray-700 text-white active:scale-95'
            "
            :data-row="r"
            :data-col="c"
          ></button>
        </template>
      </template>
    </div>

    <div v-else class="text-5xl font-extrabold text-red-400 animate-bounce">
      Game Over!
    </div>

    <div v-if="results" class="mt-4 text-white text-center">
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
import { computed, onMounted } from "vue";
import { useGameSocket } from "../composables/useGameSocket";

const props = defineProps<{ gameId: string; playerName: string }>();
const { connect, activeCell, sendReaction, results, gameOver } = useGameSocket(
  props.gameId,
  props.playerName
);

const gridSize = 5;

const rows = computed(() => Array.from({ length: gridSize }, (_, i) => i));
const cols = computed(() => Array.from({ length: gridSize }, (_, i) => i));

function isActive(r: number, c: number) {
  const cell = activeCell.value;
  return !!cell && cell.row === r && cell.col === c;
}

function handleClick(row: number, col: number) {
  if (isActive(row, col)) {
    sendReaction(row, col);
  }
}

onMounted(() => connect());
</script>
