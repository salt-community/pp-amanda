<template>
  <div>
    <button
      type="button"
      @click="openPopup"
      class="border-4 border-dashed border-amber-600 text-amber-600 font-bold py-3 px-6 rounded-lg text-lg"
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
          <div
            class="text-xl font-bold bg-zinc-900 text-amber-600 font-mono py-3 px-6 rounded-lg mb-4 select-all tracking-widest"
          >
            Enter Code 🔗
          </div>

          <form
            @submit.prevent="handleSubmit"
            class="flex flex-col items-center space-y-3"
          >
            <input
              v-model="sessionId"
              type="text"
              placeholder="XXXX"
              maxlength="4"
              class="text-center text-2xl font-bold bg-zinc-900 border-2 border-double border-amber-600 text-amber-600 font-mono py-2 px-3 rounded-lg w-[160px] tracking-[0.3em] focus:outline-none"
            />
            <button
              type="submit"
              class="block text-amber-600 hover:text-amber-800 font-mono"
            >
              {{ mutation.isPending.value ? "Starting..." : "Join ->" }}
            </button>

            <p v-if="mutation.isSuccess.value && mutation.data.value">
              Successfully Joined! <br />
            </p>

            <p v-if="mutation.error">
              {{ mutation.error }}
            </p>
          </form>
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

const openPopup = () => {
  showPopup.value = true;
};
const closePopup = () => {
  showPopup.value = false;
};

function handleSubmit() {
  if (!sessionId.value.trim()) return;
  mutation.mutate(sessionId.value);
  router.push({
    path: `/join/${sessionId}`,
  });
}
</script>
