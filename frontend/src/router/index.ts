import { createRouter, createWebHistory } from "vue-router";
import LobbyView from "../views/LobbyView.vue";
import JoinView from "../views/JoinView.vue";
import GameView from "../views/GameView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", redirect: "/lobby" },
    { path: "/lobby", component: LobbyView },
    { path: "/join", component: JoinView },
    { path: "/join/:sessionId", component: JoinView },
    { path: "/game", component: GameView },
    { path: "/game/:gameId", component: GameView },
  ],
});

export default router;
