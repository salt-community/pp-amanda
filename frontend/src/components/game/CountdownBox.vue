<template>
  <div class="text-2xl font-mono flex items-center justify-center">
    <div
      v-if="secondsLeft === null"
      class="text-amber-600 italic text-center w-full flex justify-center"
    >
      Waiting for more people to join...
    </div>
    <div
      v-else-if="secondsLeft > 0"
      class="border-8 border-double border-amber-600 rounded-xl w-28 h-28 flex items-center justify-center text-5xl font-extrabold text-amber-600 font-mono animate-[quickr-pulse_1.2s_ease-in-out_infinite]"
    >
      {{ secondsLeft }}
    </div>
    <div
      v-else
      class="border-8 border-double border-lime-500 rounded-xl w-32 h-32 flex items-center justify-center text-5xl font-extrabold text-lime-500 font-mono animate-[quickr-burst_0.6s_ease-out_forwards]"
    >
      🎯
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onUnmounted } from "vue";

const props = defineProps<{ countdownSeconds: number | null }>();
const emit = defineEmits(["finished"]);

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
      if (secondsLeft.value <= 0) {
        emit("finished");
        clearInterval(timer!);
        timer = null;
      }
    }
  }, 1000);
}

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>
