import { getRandomName } from "@/api";
import { useQuery, useQueryClient } from "@tanstack/vue-query";

export function useRandomizedName() {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ["random-name"],
    queryFn: getRandomName,
    enabled: false,
  });

  async function prefetch() {
    await queryClient.prefetchQuery({
      queryKey: ["random-name"],
      queryFn: getRandomName,
    });
  }

  return {
    ...query,
    prefetch,
  };
}
