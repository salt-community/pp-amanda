import { useQuery } from "@tanstack/vue-query";
import { getRandomName } from "../api/gameApi";

export function useRandomizedName() {
  return useQuery({
    queryKey: ["random-name"],
    queryFn: getRandomName,
  });
}
