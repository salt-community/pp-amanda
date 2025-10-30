<template>
  <div>
    <!-- Main trigger button -->
    <button
      type="button"
      @click="openPopup"
      class="bg-blue-600 text-white py-3 px-6 rounded-lg text-lg"
    >
      <h1>START GAMING SESSION 🚀</h1>
    </button>

    <Teleport to="body">
      <div
        v-if="showPopup"
        style="
          position: fixed;
          inset: 0;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 9999;
        "
        @click.self="closePopup"
      >
        <div
          style="
            background: white;
            padding: 32px;
            border-radius: 12px;
            min-width: 320px;
            text-align: center;
          "
        >
          <h2 class="text-xl font-bold mb-4">Generate Game Code</h2>

          <!-- Generate button -->
          <button
            v-if="!mutation.isSuccess.value"
            type="button"
            @click="handleGenerate"
            :disabled="mutation.isPending.value"
            class="bg-green-600 text-white py-2 px-4 rounded mb-3"
          >
            {{ mutation.isPending.value ? "Generating..." : "Generate Code" }}
          </button>

          <!-- Display generated code -->
          <div v-if="mutation.isSuccess.value && mutation.data.value">
            <p class="text-gray-700 mb-2">Your Session Code:</p>
            <div
              class="text-3xl font-bold bg-gray-100 py-3 px-6 rounded-lg mb-3 select-all"
              style="letter-spacing: 2px"
            >
              {{ mutation.data.value.sessionId }}
            </div>

            <button
              type="button"
              @click="copyCode"
              class="bg-gray-800 text-white py-2 px-4 rounded mb-3"
            >
              {{ copied ? "Copied!" : "Copy Code" }}
            </button>

            <RouterLink
              :to="`/join/${mutation.data.value.sessionId}`"
              class="block text-blue-600 underline mt-2"
            >
              Go to Game Session
            </RouterLink>
          </div>

          <!-- Error -->
          <p v-if="mutation.error" class="text-red-600 mt-2">
            {{ mutation.error }}
          </p>

          <button
            type="button"
            @click="closePopup"
            class="mt-4 text-gray-500 underline"
          >
            Close
          </button>
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
const copied = ref(false);

const openPopup = () => (showPopup.value = true);
const closePopup = () => (showPopup.value = false);

function handleGenerate() {
  mutation.mutate();
}

function copyCode() {
  if (!mutation.data.value?.sessionId) return;
  navigator.clipboard.writeText(mutation.data.value.sessionId);
  copied.value = true;
  setTimeout(() => (copied.value = false), 2000);
}
</script>
