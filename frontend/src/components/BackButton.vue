<template>
  <button
    type="button"
    @click="goBack"
    class="flex items-center gap-1 text-sm text-amber-400 hover:text-amber-200"
  >
    ← Go Back
  </button>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();

function goBack() {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push("/");
  }
}
</script>
