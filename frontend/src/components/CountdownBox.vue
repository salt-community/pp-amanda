<template>
  <div v-if="state === 'waiting'">Waiting for game to start...</div>
  <div v-else-if="state === 'countdown'">
    Game starts in {{ formattedTime }}
  </div>
  <div v-else>Go!</div>
</template>

<script setup lang="ts">
import { ref, watch, onUnmounted, computed } from "vue";

const props = defineProps<{
  startTime: Date | null;
}>();

const secondsLeft = ref<number | null>(null);
let timer: number | null = null;

const state = ref<"waiting" | "countdown" | "started">("waiting");

watch(
  () => props.startTime,
  (newTime) => {
    if (!newTime) return;
    state.value = "countdown";
    startCountdown(newTime);
  }
);

function startCountdown(target: Date) {
  timer = window.setInterval(() => {
    const diff = Math.floor((target.getTime() - Date.now()) / 1000);
    secondsLeft.value = Math.max(diff, 0);
    if (diff <= 0 && timer) {
      clearInterval(timer);
      timer = null;
    }
  }, 1000);
}

onUnmounted(() => {
  if (timer) clearInterval(timer);
});

const formattedTime = computed(() => {
  if (secondsLeft.value === null) return null;
  const minutes = Math.floor(secondsLeft.value / 60);
  const seconds = secondsLeft.value % 60;
  return `${minutes}:${seconds.toString().padStart(2, "0")}`;
});
</script>
