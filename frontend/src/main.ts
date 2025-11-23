import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import vueQuery from "./plugins/vueQuery";
import "./style.css";
import "./assets/quickr.css";

if (typeof (globalThis as any).global === "undefined") {
  (window as any).global = window;
}

const app = createApp(App);
app.use(router);
app.use(vueQuery);
app.mount("#app");
