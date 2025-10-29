<template>
  <div>
    <button type="button" @click="openPopup">
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
            padding: 20px;
            border-radius: 8px;
            min-width: 300px;
          "
        >
          <h2>START GAMING SESSION 🚀</h2>
          <form @submit.prevent="handleSubmit">
            <input
              v-model="roomName"
              type="text"
              placeholder="Enter Session Name"
            />
            <button
              type="submit"
              :disabled="mutation.isPending.value"
              class="bg-blue-600 text-white py-2 px-4 rounded mt-3"
            >
              {{ mutation.isPending.value ? "Starting..." : "Create Session" }}
            </button>
            <p v-if="mutation.isSuccess.value && mutation.data.value">
              INVITATION CODE {{ mutation.data.value.sessionId }}

              <RouterLink :to="`/join/${mutation.data.value.sessionId}`"
                >GO TO GAME SESSION</RouterLink
              >
            </p>

            <p v-if="mutation.error">
              {{ mutation.error }}
            </p>
            <button
              type="button"
              @click="closePopup"
              :disabled="mutation.isPending.value"
            >
              Close
            </button>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useCreateSession } from "../composables/useCreateSession";

const roomName = ref("");
const mutation = useCreateSession();
const showPopup = ref(false);

const openPopup = () => {
  showPopup.value = true;
};
const closePopup = () => {
  showPopup.value = false;
};

function handleSubmit() {
  if (!roomName.value.trim()) return;
  mutation.mutate(roomName.value);
  roomName.value = "";
}
</script>
