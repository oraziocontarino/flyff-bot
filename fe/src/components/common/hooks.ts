import { useMemo } from "react";
import { useFetchConfigurationQuery } from "../../api/pipeline/hooks";

export const useSelectConfig = (id:number) => {
  const {data, isLoading, isError, error} = useFetchConfigurationQuery();

  const pipeData = useMemo(() =>
    data?.find(item => item.pipeline.id === id),
    [data, id]
  );

  return { pipeData, isLoading, isError, error};
}
