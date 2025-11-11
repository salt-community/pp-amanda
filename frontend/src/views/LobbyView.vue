<template>
  <div
    class="min-h-screen flex flex-col items-center justify-start pt-60 sm:justify-center sm:pt-0 gap-6 sm:gap-10"
  >
    <CreateSession />
    <JoinSession />
  </div>
</template>

<script setup>
import JoinSession from "../components/JoinSession.vue";
import CreateSession from "../components/CreateSession.vue";
</script>
