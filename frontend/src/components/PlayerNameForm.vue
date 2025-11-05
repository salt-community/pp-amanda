<template>
  <div class="flex flex-col items-center">
    <h2 class="text-xl font-semibold mb-4">Enter your name to join</h2>

    <form @submit.prevent="submitName" class="flex gap-2">
      <input
        v-model="playerName"
        placeholder="Your name"
        class="border rounded px-3 py-2"
        :disabled="isPending || joinClosed"
      />
      <button
        type="submit"
        class="bg-blue-500 text-white px-4 py-2 rounded"
        :disabled="isPending || joinClosed"
      >
        <span v-if="isPending">Joining...</span>
        <span v-else>Join</span>
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
  (e: "joined", gameResponse: GameResponse): void;
}>();

const playerName = ref("");
const { mutateAsync: join, isPending } = usePlayerJoin(props.sessionId);
const { joinClosed } = useJoinDeadline(props.sessionId);

async function submitName() {
  if (!playerName.value || joinClosed.value) return;

  try {
    const response = await join(playerName.value);
    emit("joined", response);
  } catch (error) {
    console.error("Failed to join game:", error);
  }
}
</script>
