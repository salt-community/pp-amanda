<template>
  <div class="flex flex-col gap-4 text-center">
    <h2 class="text-lg font-semibold text-gray-800">QuickR ⚡️</h2>
    <p class="text-sm text-gray-600 max-w-md mx-auto">
      Who's the quickeZt - Let's find out 🚀
    </p>

    <button
      @click="startGame"
      class="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition"
      :disabled="mutation.isPending.value"
    >
      {{ mutation.isPending.value ? "Starting..." : "LET'S PLAY" }}
    </button>

    <p v-if="mutation.error" class="text-red-500 text-xs mt-2">
      {{ mutation.error }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { useSelectGameType } from "../composables/useSelectGameType";

const props = defineProps<{ sessionId: string }>();
const emit = defineEmits<{ (e: "initialized", gameType: "REACTION"): void }>();

const mutation = useSelectGameType();

function startGame() {
  mutation.mutate(
    { sessionId: props.sessionId, gameType: "REACTION" },
    {
      onSuccess: () => emit("initialized", "REACTION"),
    }
  );
}
</script>
