<template>
  <div>
    <button
      type="button"
      @click="openPopup"
      class="border-4 border-dashed border-amber-600 text-white py-3 px-6 rounded-lg text-lg"
    >
      <h1>JOIN VIA INVITATION CODE 🔗</h1>
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
            padding: 20px;
            border-radius: 8px;
            min-width: 300px;
          "
        >
          <h2>Enter Code 🔗</h2>

          <form @submit.prevent="handleSubmit">
            <input v-model="sessionId" type="text" placeholder="Enter Code" />
            <button type="submit">
              {{ mutation.isPending.value ? "Starting..." : "Join" }}
            </button>

            <p v-if="mutation.isSuccess.value && mutation.data.value">
              Successfully Joined! <br />
            </p>

            <p v-if="mutation.error">
              {{ mutation.error }}
            </p>

            <!--             <button
              type="button"
              @click="closePopup"
              :disabled="mutation.isPending.value"
            >
              Close
            </button> -->
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
