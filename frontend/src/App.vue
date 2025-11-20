<template>
  <div
    class="min-h-screen bg-space bg-cover bg-center bg-no-repeat font-mono text-white"
  >
    <div
      class="bg-black/40 flex flex-col items-center gap-6 justify-center h-full"
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
