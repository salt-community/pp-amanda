<template>
  <div>
    <button
      type="button"
      @click="openPopup"
      class="border-4 border-dashed border-amber-600 text-amber-600 font-bold py-3 px-6 rounded-lg text-lg hover:bg-zinc-800 transition"
    >
      <h1>🟠 JOIN VIA INVITATION CODE 🟠</h1>
    </button>
    <Teleport to="body">
      <div
        v-if="showPopup"
        @click.self="closePopup"
        class="fixed inset-0 bg-black/50 flex items-center justify-center z-[9999]"
      >
        <div
          class="bg-zinc-900 border-4 border-dashed border-amber-600 p-8 rounded-2xl w-[320px] h-[220px] text-center shadow-2xl relative flex flex-col items-center justify-center"
        >
          <div class="text-xl text-amber-600 font-mono mb-4 tracking-widest">
            ENTER CODE:
          </div>
          <form
            @submit.prevent="handleSubmit"
            class="flex flex-col items-center space-y-3"
          >
            <input
              v-model="sessionId"
              type="text"
              inputmode="numeric"
              pattern="[0-9]*"
              maxlength="4"
              placeholder="XXXX"
              @input="sessionId = sessionId.replace(/\D/g, '')"
              class="text-center text-2xl font-extrabold bg-zinc-900 border-8 border-double border-amber-600 text-amber-600 font-mono tabular-nums py-3 px-8 rounded-lg tracking-[0.3em] placeholder-amber-600 placeholder-opacity-70 w-[180px] focus:outline-none"
            />

            <button
              type="submit"
              class="mt-3 text-lg text-amber-600 hover:text-amber-400 font-mono transition"
            >
              {{ mutation.isPending.value ? "Connecting..." : "Proceed →" }}
            </button>
          </form>
          <div
            v-if="mutation.isSuccess.value && mutation.data.value"
            class="mt-4 text-green-500 font-mono text-sm animate-pulse"
          ></div>
          <p v-if="mutation.error" class="text-red-600 text-sm mt-3">
            {{ mutation.error.message || mutation.error }}
          </p>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useJoinSession } from "../composables/useJoinSession";
import router from "../router";

const sessionId = ref("");
const mutation = useJoinSession();
const showPopup = ref(false);

const openPopup = () => (showPopup.value = true);
const closePopup = () => (showPopup.value = false);

function handleSubmit() {
  if (!sessionId.value.trim()) return;
  mutation.mutate(sessionId.value);
  router.push({ path: `/join/${sessionId.value}` });
}
</script>
