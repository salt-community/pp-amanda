<template>
  <div
    class="min-h-screen bg-space bg-cover bg-center bg-no-repeat font-mono text-white"
  >
    <div class="bg-black/40 min-h-screen relative md:flex md:flex-col">
      <header class="fixed top-0 w-full z-50 md:static">
        <QuickrHeader />
      </header>

      <main
        class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-full flex flex-col items-center justify-center gap-6 px-4 md:static md:flex-1 md:translate-x-0 md:translate-y-0 md:top-auto md:left-auto"
      >
        <RouterView />
      </main>

      <footer class="fixed bottom-0 w-full z-50 md:static">
        <QuickrFooter />
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { QuickrFooter, QuickrHeader } from "@/components";
</script>
