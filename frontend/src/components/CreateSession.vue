<template>
  <div>
    <button
      type="button"
      @click="openPopup"
      class="bg-amber-600 text-white py-3 px-6 rounded-lg text-lg"
    >
      <h1>START NEW GAMING SESSION 🚀</h1>
    </button>

    <Teleport to="body">
      <div
        v-if="showPopup"
        @click.self="closePopup"
        class="fixed inset-0 bg-black/50 flex items-center justify-center z-[9999]"
      >
        <div
          class="bg-zinc-900 border-2 border-dashed border-amber-600 p-8 rounded-2xl w-[320px] h-[200px] text-center shadow-2xl relative flex flex-col items-center justify-center"
        >
          <button
            v-if="!mutation.isSuccess.value"
            type="button"
            @click="handleGenerate"
            :disabled="mutation.isPending.value"
            class="bg-zinc-800 hover:bg-zinc-600 disabled:bg-amber-500 border-2 border-double border-amber-600 text-amber-600 font-mono py-3 px-6 rounded-lg mb-4 select-all tracking-widest"
          >
            {{ mutation.isPending.value ? "Generating..." : "Get Code" }}
          </button>

          <div v-if="mutation.isSuccess.value && mutation.data.value">
            <div
              class="text-6xl font-bold bg-zinc-900 border-2 border-double border-amber-600 text-amber-600 font-mono py-3 px-6 rounded-lg mb-4 select-all tracking-widest"
            >
              {{ mutation.data.value.sessionId }}
            </div>
            <RouterLink
              :to="`/join/${mutation.data.value.sessionId}`"
              class="block text-amber-600 hover:text-amber-800 font-mono"
            >
              To Game Session ->
            </RouterLink>
          </div>

          <!-- Error message -->
          <p v-if="mutation.error" class="text-red-600 mt-3">
            {{ mutation.error }}
          </p>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useCreateSession } from "../composables/useCreateSession";

const mutation = useCreateSession();
const showPopup = ref(false);

const openPopup = () => (showPopup.value = true);
const closePopup = () => (showPopup.value = false);

function handleGenerate() {
  mutation.mutate();
}
</script>
