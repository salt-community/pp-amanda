<template>
  <div
    class="min-h-screen bg-gradient-to-b from-slate-900 to-slate-800 text-white flex flex-col items-center justify-center gap-6"
  >
    <h1 class="text-4xl font-extrabold tracking-wide">⚡ Reaction Game ⚡</h1>
    <div class="text-sm text-gray-400 mt-4">Active: {{ activeCell }}</div>
    <div class="grid grid-cols-3 gap-4">
      <!-- 🎯 Game Grid -->
      <div v-if="!gameOver" class="grid grid-cols-3 gap-2">
        <div v-for="r in 3" :key="r" class="flex gap-2">
          <button
            v-for="c in 3"
            :key="`${r}-${c}`"
            @click="handleClick(r - 1, c - 1)"
            class="w-24 h-24 rounded-xl border border-gray-700 font-bold text-xl flex items-center justify-center shadow-inner transition-all duration-200 ease-in-out select-none"
            :class="{
              'bg-lime-400 scale-110 shadow-lg ring-4 ring-lime-500 animate-pulse':
                activeCell?.row === r - 1 && activeCell?.col === c - 1,
              'bg-gray-800 hover:bg-gray-700 text-white active:scale-95': !(
                activeCell?.row === r - 1 && activeCell?.col === c - 1
              ),
            }"
          ></button>
        </div>
      </div>

      <!-- 🏁 Game Over -->
      <div v-else lass="text-5xl font-extrabold text-red-400 animate-bounce">
        > Game Over!
      </div>

      <!-- 🧮 Scoreboard -->
      <div v-if="results" class="mt-4 text-white text-center">
        <h3 class="font-bold mb-2">Scores:</h3>
        <ul>
          <li v-for="(score, player) in results" :key="player">
            {{ player }}: {{ score }}
          </li>
        </ul>
      </div>
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
  if (activeCell.value?.row === row && activeCell.value?.col === col) {
    sendReaction(row, col);
  }
}
</script>
