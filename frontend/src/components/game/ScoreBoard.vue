<template>
  <div class="text-center w-full max-w-xl mx-auto mt-8">
    <h2 class="text-2xl font-bold text-amber-500 mb-4 text-center">
      Final Results
    </h2>

    <p v-if="isLoading">Loading...</p>
    <p v-else-if="isError">Something went wrong 🥺</p>

    <ul v-else-if="result" class="space-y-2">
      <li
        v-for="([name, score], index) in Object.entries(result.results)"
        :key="name"
        class="text-lg"
      >
        {{ getMedal(index) }} {{ name }} – {{ score.toFixed(0) }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { useScoreBoard } from "@/composables";

const props = defineProps<{ gameId: string }>();

const { data: result, isLoading, isError } = useScoreBoard(props.gameId);

function getMedal(index: number) {
  if (index === 0) return "🥇";
  if (index === 1) return "🥈";
  if (index === 2) return "🥉";
  return "🐌";
}
</script>
