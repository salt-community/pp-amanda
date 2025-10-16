import { VueQueryPlugin, QueryClient } from "@tanstack/vue-query";
import type { VueQueryPluginOptions } from "@tanstack/vue-query";

const queryClient = new QueryClient();

const vueQueryOptions: VueQueryPluginOptions = {
  queryClient,
};

export default {
  install(app: any) {
    app.use(VueQueryPlugin, vueQueryOptions);
  },
};
