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
        Join
      </button>
    </form>

    <p v-if="joinClosed" class="text-red-500 mt-2">Join period has ended!</p>
  </div>
</template>

<script setup lang="ts">
import { useJoinStatus } from "../composables/useJoinStatus";
import { ref } from "vue";
import { usePlayerJoin } from "../composables/usePlayerJoin";

const props = defineProps<{ sessionId: string }>();
const emit = defineEmits<{
  (e: "joined", name: string): void;
}>();

const playerName = ref("");
const { mutateAsync: join, isPending } = usePlayerJoin(props.sessionId);
const { joinClosed } = useJoinStatus(props.sessionId);

async function submitName() {
  if (!playerName.value || joinClosed.value) return;
  await join(playerName.value);
  emit("joined", playerName.value);
}
</script>
