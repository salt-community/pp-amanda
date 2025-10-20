<template>
  <div>
    <button
      type="button"
      @click="openPopup"
      class="bg-blue-600 text-white py-2 px-4 rounded mt-3"
    >
      <h1>{{ "JOIN VIA INVITATION CODE 🔗" }}</h1>
    </button>

    <Teleport to="body">
      <div v-if="showPopup">
        <div>
          <h2>JOIN VIA INVITATION CODE 🔗</h2>
          <form @submit.prevent="handleSubmit">
            <input
              v-model="sessionId"
              type="text"
              placeholder="Enter Invitation Code"
            />
            <button
              type="submit"
              class="bg-blue-600 text-white py-2 px-4 rounded mt-3"
            >
              {{ mutation.isPending.value ? "Starting..." : "Join Session" }}
            </button>
            <p v-if="mutation.isSuccess && mutation.data">
              Successfully Join
              <RouterLink class="router-link" :to="`/game/${sessionId}`"
                >Take Me To Game Session</RouterLink
              >
            </p>

            <p v-if="mutation.error">
              {{ mutation.error }}
            </p>
            <button
              type="button"
              @click="closePopup"
              :disabled="mutation.isPending.value"
              class="bg-blue-600 text-white py-2 px-4 rounded mt-3"
            >
              {{ mutation.isPending.value ? "Starting..." : "Create Session" }}
            </button>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, Teleport } from "vue";
import { useJoinSession } from "../composables/useJoinSession";
import { RouterLink } from "vue-router";

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
}
</script>
