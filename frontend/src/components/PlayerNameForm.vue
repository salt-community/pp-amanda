<template>
  <div class="flex flex-col items-center">
    <div class="text-amber-600 font-bold tracking-widest text-xl mb-4">
      ENTER NAME:
    </div>

    <form
      @submit.prevent="submitName"
      class="flex flex-col items-center space-y-3"
    >
      <input
        v-model="playerName"
        maxlength="8"
        placeholder="?"
        class="quickr-input w-[220px]"
      />
      <button type="submit" class="quickr-button">
        <span v-if="isPending">Joining...</span>
        <span v-else>Join →</span>
      </button>
    </form>

    <p v-if="joinClosed" class="text-red-500 mt-2">Join period has ended!</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useJoinDeadline } from "../composables/useJoinDeadline";
import { usePlayerJoin } from "../composables/usePlayerJoin";
import type { GameResponse } from "../types/game";

const props = defineProps<{ sessionId: string }>();

const emit = defineEmits<{
  (
    e: "joined",
    payload: { gameResponse: GameResponse; playerName: string }
  ): void;
}>();

const playerName = ref("");
const { mutateAsync: join, isPending } = usePlayerJoin(props.sessionId);
const { joinClosed } = useJoinDeadline(props.sessionId);

async function submitName() {
  if (!playerName.value || joinClosed.value) return;

  try {
    const response = await join(playerName.value);
    emit("joined", { gameResponse: response, playerName: playerName.value });
  } catch (error) {
    console.error("Failed to join game:", error);
  }
}
</script>
