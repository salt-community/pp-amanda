import { ref, onMounted, onUnmounted, computed } from "vue";

export function useJoinDeadline(joinDeadline: string | null) {
  const timeLeft = ref<number | null>(null);
  const joinClosed = computed(
    () => timeLeft.value !== null && timeLeft.value <= 0
  );

  let interval: number | null = null;

  const updateTimeLeft = () => {
    if (!joinDeadline) return;
    const diff = new Date(joinDeadline).getTime() - Date.now();
    timeLeft.value = diff;
    if (diff <= 0 && interval) clearInterval(interval);
  };

  onMounted(() => {
    updateTimeLeft();
    interval = window.setInterval(updateTimeLeft, 1000);
  });

  onUnmounted(() => {
    if (interval) clearInterval(interval);
  });

  return { timeLeft, joinClosed };
}
