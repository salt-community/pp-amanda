import { createSession } from "@/api";
import { useMutation } from "@tanstack/vue-query";

export function useCreateSession() {
  return useMutation({
    mutationFn: () => createSession(),
    onSuccess: (data) => {
      data;
    },
  });
}
