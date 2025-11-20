import { useQuery } from "@tanstack/vue-query";
import { getTopList } from "../api/gameApi";

export function useTopList() {
  return useQuery({
    queryKey: ["toplist"],
    queryFn: getTopList,
  });
}
