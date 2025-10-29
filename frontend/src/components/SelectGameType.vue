<template>
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
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useSelectGameType } from "../composables/useSelectGameType";
import type { GameType } from "../types/game";

const props = defineProps<{ sessionId: string }>();
const emit = defineEmits<{ (e: "initialized", gameType: GameType): void }>();

const selectedType = ref<GameType | "">("");
const mutation = useSelectGameType();
const availableGameTypes: GameType[] = ["REACTION"];

function handleSubmit() {
  if (!selectedType.value) return;
  mutation.mutate(
    { sessionId: props.sessionId, gameType: selectedType.value },
    {
      onSuccess: () => emit("initialized", selectedType.value as GameType),
    }
  );
}
</script>
