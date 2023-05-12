
import api from '../core';
import { fetchConfiguration, fetchWindowList, updateSelectedWindow } from './endpoints';

export const pipelineApi = api.injectEndpoints({
  endpoints: (builder) => ({
    fetchConfiguration: fetchConfiguration(builder),
    fetchWindowList: fetchWindowList(builder),
    updateSelectedWindow: updateSelectedWindow(builder),
  }),
});
