<template>
  <div class="flex flex-col items-center space-y-6 text-center">
    <div class="flex flex-col items-center space-y-2">
      <div
        v-if="isRandomPending"
        class="text-amber-400 font-mono text-xl animate-pulse"
      >
        Generating random name...
      </div>

      <div v-else class="transition-all duration-500 ease-out">
        <div class="text-amber-500 text-lg font-semibold tracking-widest mb-4">
          Join As
        </div>
        <div class="border-8 border-double border-amber-800 rounded-lg p-4">
          <div
            class="font-mono text-amber-400 text-3xl mt-1 drop-shadow-lg select-none"
          >
            {{ randomName }}
          </div>
        </div>
        <button
          type="button"
          @click="refreshName"
          class="text-amber-500 text-5xl mt-2 hover:scale-110 hover:text-amber-300 transition-transform duration-300"
          :disabled="isRandomPending"
          title="Generate another random name"
        >
          ⟳
        </button>
      </div>
    </div>
    <button
      @click="submitName"
      class="mt-4 bg-amber-600 text-black font-bold px-6 py-2 rounded-lg shadow-md hover:bg-amber-500 hover:scale-105 active:scale-95 transition-all duration-200"
      :disabled="isPending || joinClosed || !randomName"
    >
      <span v-if="isPending">Connecting...</span>
      <span v-else>Proceed To Game →</span>
    </button>

    <p v-if="joinClosed" class="text-red-500 mt-3 text-sm italic">
      Join period has ended.
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { useJoinDeadline } from "../composables/useJoinDeadline";
import { usePlayerJoin } from "../composables/usePlayerJoin";
import { useRandomizedName } from "../composables/useRandomizedName";
import type { GameResponse } from "../types/game";

const props = defineProps<{ sessionId: string }>();

const emit = defineEmits<{
  (
    e: "joined",
    payload: { gameResponse: GameResponse; playerName: string }
  ): void;
}>();

const { data, refetch, isPending: isRandomPending } = useRandomizedName();
const randomName = ref("");

watch(data, (newName) => {
  if (newName) randomName.value = newName;
});

onMounted(() => refetch());

function refreshName() {
  refetch();
}

const { mutateAsync: join, isPending } = usePlayerJoin(props.sessionId);
const { joinClosed } = useJoinDeadline(props.sessionId);

async function submitName() {
  if (!randomName.value || joinClosed.value) return;

  try {
    const response = await join(randomName.value);
    emit("joined", { gameResponse: response, playerName: randomName.value });
  } catch (error) {
    console.error("Failed to join game:", error);
  }
}
</script>
