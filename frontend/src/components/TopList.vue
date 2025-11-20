<template>
  <div class="w-full max-w-xl mx-auto mt-8">
    <h2 class="text-2xl font-bold text-amber-500 mb-4 text-center">
      Top 10 All-Time Scores
    </h2>

    <p v-if="isLoading">Loading scores...</p>
    <p v-else-if="isError">Could not load scores 🥲</p>

    <table v-else class="w-full text-left border-collapse">
      <thead>
        <tr class="border-b border-amber-600">
          <th class="py-2">#</th>
          <th class="py-2">Player</th>
          <th class="py-2 text-right">Score</th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="(item, idx) in data"
          :key="item.playerName + idx"
          class="border-b border-zinc-800"
        >
          <td class="py-2">
            <span v-if="idx === 0">🥇</span>
            <span v-else-if="idx === 1">🥈</span>
            <span v-else-if="idx === 2">🥉</span>
            <span v-else>{{ idx + 1 }}</span>
          </td>
          <td class="py-2">{{ item.playerName }}</td>
          <td class="py-2 text-right font-mono">{{ item.score }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { useTopList } from "../composables/useTopList";

const { data, isLoading, isError } = useTopList();
</script>
