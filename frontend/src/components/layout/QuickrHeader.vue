<template>
  <header class="w-full border-b-4 border-amber-600">
    <div class="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between">
      <RouterLink to="/" class="flex items-center gap-2">
        <span class="text-amber-500 text-2xl">⚡</span>
        <span class="font-bold text-xl tracking-wide">Quickr</span>
      </RouterLink>

      <nav class="flex gap-4 text-sm">
        <RouterLink to="/" class="hover:text-amber-300">Home</RouterLink>
        <RouterLink to="/top-list" class="hover:text-amber-300"
          >Top List</RouterLink
        >
        <RouterLink to="/about" class="hover:text-amber-300">About</RouterLink>
      </nav>
    </div>
  </header>
</template>

<script setup lang="ts">
import { RouterLink } from "vue-router";
</script>
