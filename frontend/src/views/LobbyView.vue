<template>
  <div
    class="min-h-screen flex flex-col items-center justify-start pt-40 sm:justify-center sm:pt-0 gap-6 sm:gap-10"
  >
    <div
      v-if="$route.query.expired"
      class="text-red-500 font-mono text-sm italic mb-4 text-center"
    >
      "Deadline passed for this session..."
    </div>
    <CreateSession />
    <JoinSession />
  </div>
</template>

<script setup>
import { JoinSession, CreateSession } from "@/components";
</script>
