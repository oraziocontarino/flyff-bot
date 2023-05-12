import { createSelector } from "@reduxjs/toolkit";
import { pipelineApi } from ".";

export const windowListSelector = createSelector(
  pipelineApi.endpoints.fetchWindowList.select(),
  response => response?.data
)
