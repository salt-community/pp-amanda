<script setup lang="ts">
import { useScoreBoard } from "../composables/useScoreBoard";

const props = defineProps<{ gameId: string }>();

const { data: result, isLoading, isError } = useScoreBoard(props.gameId);
</script>

<template>
  <div class="text-center">
    <p v-if="isLoading">Loading...</p>
    <p v-else-if="isError">Something went wrong 😢</p>

    <ul v-else>
      <li
        v-for="([name, time], index) in Object.entries(result.results)"
        :key="name"
      >
        {{ index + 1 }}. {{ name }} — {{ time.toFixed(2) }}s
      </li>
    </ul>
  </div>
</template>
