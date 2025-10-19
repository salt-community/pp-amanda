<template>
  <div>
    <h1>Start Gaming Session 🚀</h1>

    <form @submit.prevent="handleSubmit">
      <input v-model="roomName" type="text" placeholder="Enter Session Name" />
      <button
        type="submit"
        :disabled="mutation.isPending.value"
        class="bg-blue-600 text-white py-2 px-4 rounded mt-3"
      >
        {{ mutation.isPending.value ? "Starting..." : "Create Session" }}
      </button>
    </form>

    <p v-if="mutation.isSuccess && mutation.data">
      SessionID: {{ mutation.data }}
    </p>

    <p v-if="mutation.error">
      {{ mutation.error }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useCreateRoom } from "../composables/useCreateSession";

const roomName = ref("");
const mutation = useCreateRoom();

function handleSubmit() {
  if (!roomName.value.trim()) return;
  mutation.mutate(roomName.value);
  roomName.value = "";
}
</script>
