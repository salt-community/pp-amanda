<template>
  <div
    class="min-h-screen bg-[url('/src/assets/bg-space.jpg')] bg-cover bg-center bg-no-repeat font-mono text-white"
  >
    <div
      class="min-h-screen bg-black/40 flex flex-col items-center gap-6 justify-center"
    >
      <QuickrHeader />
      <RouterView />
      <QuickrFooter />
    </div>
  </div>
</template>

<script setup lang="ts">
import { QuickrFooter, QuickrHeader } from "@/components";
</script>
