import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import vueQuery from "./plugins/vueQuery";

const app = createApp(App);
app.use(router);
app.use(vueQuery);
app.mount("#app");
