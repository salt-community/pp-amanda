import { createRouter, createWebHistory } from "vue-router";
import LobbyView from "../views/LobbyView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", redirect: "/lobby" },
    { path: "/lobby", component: LobbyView },
  ],
});

export default router;
