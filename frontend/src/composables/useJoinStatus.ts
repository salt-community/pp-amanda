import { useGameStatus } from "./useGameStatus";
import { computed } from "vue";

export function useJoinStatus(sessionId: string) {
  const { data } = useGameStatus(sessionId);

  const joinClosed = computed(() => {
    if (!data.value?.joinDeadline) return false;
    const deadline = new Date(data.value.joinDeadline).getTime();
    return Date.now() > deadline;
  });

  return { data, joinClosed };
}
