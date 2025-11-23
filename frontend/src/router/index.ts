import { createRouter, createWebHistory } from "vue-router";
import { AboutView, GameView, JoinView, LobbyView, TopListView } from "@/views";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", redirect: "/lobby" },
    { path: "/lobby", component: LobbyView },
    { path: "/top-list", component: TopListView },
    { path: "/about", component: AboutView },
    { path: "/join", component: JoinView },
    { path: "/join/:sessionId", component: JoinView },
    { path: "/game", component: GameView },
    { path: "/game/:gameId", component: GameView },
  ],
});

export default router;
