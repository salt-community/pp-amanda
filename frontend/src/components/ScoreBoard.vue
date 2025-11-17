<script setup lang="ts">
import { useScoreBoard } from "../composables/useScoreBoard";

const props = defineProps<{ gameId: string }>();

const { data: result, isLoading, isError } = useScoreBoard(props.gameId);

function getMedal(index: number) {
  if (index === 0) return "🥇";
  if (index === 1) return "🥈";
  if (index === 2) return "🥉";
  return "🐌";
}
</script>

<template>
  <div class="text-center">
    <p v-if="isLoading">Loading...</p>
    <p v-else-if="isError">Something went wrong 🥺</p>

    <ul v-else-if="result">
      <li
        v-for="([name, time], index) in Object.entries(result.results)"
        :key="name"
      >
        {{ getMedal(index) }} {{ name }} – {{ time.toFixed(2) }}
      </li>
    </ul>
  </div>
</template>
