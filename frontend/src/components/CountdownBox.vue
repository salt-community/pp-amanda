<template>
  <div class="text-2xl font-mono">
    <template v-if="secondsLeft === null">
      Waiting for game to start...
    </template>
    <template v-else-if="secondsLeft > 0">
      Starting in {{ secondsLeft }}...
    </template>
    <template v-else> 🎯 Go! </template>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onUnmounted } from "vue";

const props = defineProps<{ countdownSeconds: number | null }>();

const secondsLeft = ref<number | null>(null);
let timer: number | null = null;

watch(
  () => props.countdownSeconds,
  (newSeconds) => {
    if (newSeconds === null) return;
    if (timer) clearInterval(timer);
    secondsLeft.value = newSeconds;
    startCountdown();
  }
);

function startCountdown() {
  timer = window.setInterval(() => {
    if (secondsLeft.value !== null) {
      secondsLeft.value -= 1;
      if (secondsLeft.value <= 0 && timer) {
        clearInterval(timer);
        timer = null;
      }
    }
  }, 1000);
}

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>
