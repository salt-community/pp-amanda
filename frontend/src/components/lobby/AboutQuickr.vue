<template>
  <div class="flex flex-col items-center text-center gap-6">
    <h2 class="text-3xl font-bold">How to play Quickr?</h2>

    <p class="text-lg max-w-md">
      It’s simple! Just tap the active cells as fast as you can 💥
    </p>

    <div class="w-20 h-1 bg-amber-600 rounded-full"></div>

    <img
      :src="gif"
      alt="Quick-R gameplay demo"
      class="rounded-lg w-full max-w-md bg-black p-0.5"
    />
  </div>
</template>

<script setup lang="ts">
import gif from "@/assets/quickr.gif";
</script>
