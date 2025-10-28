<template>
  <Teleport to="body">
    <div
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-[9999]"
      @click.self="closePopup"
    >
      <div class="bg-white p-5 rounded-lg min-w-[300px] text-center">
        <h2 class="text-lg font-semibold mb-3">Pick A Game 🎮</h2>

        <form @submit.prevent="handleSubmit" class="flex flex-col gap-2">
          <label for="gameType" class="text-sm">Select game type:</label>

          <select
            id="gameType"
            v-model="selectedType"
            class="border p-2 rounded text-sm"
            required
          >
            <option disabled value="">games</option>
            <option
              v-for="option in availableGameTypes"
              :key="option"
              :value="option"
            >
              {{ option }}
            </option>
          </select>

          <button
            type="submit"
            class="bg-blue-600 text-white py-1.5 px-3 rounded mt-2"
            :disabled="mutation.isPending.value"
          >
            {{ mutation.isPending.value ? "Initializing..." : "Start Game" }}
          </button>

          <p v-if="mutation.error" class="text-red-500 text-xs mt-2">
            {{ mutation.error }}
          </p>
        </form>

        <button
          type="button"
          @click="closePopup"
          class="text-gray-500 text-xs underline mt-3"
        >
          Cancel
        </button>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useInitializeGame } from "../composables/useInitializeGame";
import type { GameType } from "../types/game";

const props = defineProps<{ sessionId: string }>();
const emit = defineEmits<{ (e: "initialized", gameType: GameType): void }>();

//const showPopup = ref(true);
const selectedType = ref<GameType | "">("");
const mutation = useInitializeGame();

const availableGameTypes: GameType[] = ["REACTION"];

function closePopup() {
  emit("initialized", selectedType.value as GameType);
}
function handleSubmit() {
  if (!selectedType.value) return;
  mutation.mutate(
    { sessionId: props.sessionId, gameType: selectedType.value },
    {
      onSuccess: () => {
        emit("initialized", selectedType.value as GameType);
      },
    }
  );
}
</script>
