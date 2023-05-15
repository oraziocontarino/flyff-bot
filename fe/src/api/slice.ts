import { createSelector, createSlice } from '@reduxjs/toolkit';
import { Configuration, WindowItem } from '../api/types';
import { RootState } from '../store';
import { deepEqual } from './utils';

interface ConfigurationState {
  isConnected?:boolean;
  configurations: Configuration[];
  windows: WindowItem[];
}

const initialState: ConfigurationState = {
  configurations: [],
  windows: []
};

const configurationSlice = createSlice({
  name: 'flyffBot',
  initialState,
  reducers: {
    storeConnectionSatus: (state, args:{type:string, payload:{isConnected:boolean}}) => {
      state.isConnected = args.payload.isConnected;
    },
    storeConfigurations: (state, args:{type:string, payload:Configuration[]}) => {
      if(!deepEqual(state.configurations, args.payload)) {
        state.configurations = args.payload;
      }
    },
    storeWindowList: (state, args:{type:string, payload:WindowItem[]}) => {
      if(!deepEqual(state.windows, args.payload)) {
        state.windows = args.payload;
      }
    },
  },
});

const rootSelector = (state: RootState) => state.flyffBot;

const isConnectedSelector = createSelector(rootSelector, (state) => (
  state.isConnected
));

const windowsSelector = createSelector(rootSelector, (state) => (
  state.windows
));

const configurationSelector = (pipelineId:number) =>  createSelector(rootSelector, (state) => (
  state.configurations.find(item => item.pipeline.id === pipelineId)
));

const pipelineConfigurationSelector = (pipelineId:number) => createSelector(rootSelector, (state) => (
  state.configurations.find(item => item.pipeline.id === pipelineId)?.pipeline
));

const hotkeysConfigurationSelector = (pipelineId:number) => createSelector(rootSelector, (state) => (
  state.configurations.find(item => item.pipeline.id === pipelineId)?.hotkeys
));

const customActionSlotsConfigurationSelector = (pipelineId:number) => createSelector(rootSelector, (state) => (
  state.configurations.find(item => item.pipeline.id === pipelineId)?.customActionSlots
));

const isSelectedAndExistsSelector = (pipelineId:number) => createSelector(
  pipelineConfigurationSelector(pipelineId),
  windowsSelector,
  (pipeline, windows) => {
    const selectedHwnd = pipeline?.selectedWindowHwnd;
    const exists = windows.some(item => item.hwnd === selectedHwnd);
    //console.log("@@@ debug auto deselect: ["+pipeline?.id+"]", {selectedHwnd, exists, windows});
    return selectedHwnd != null && exists;
  }
);

const customActionSlotCountSelector = (pipelineId:number) => createSelector(
  customActionSlotsConfigurationSelector(pipelineId),
  (customActionSlots) => customActionSlots?.length ?? 0
);

const customActionSlotStatusSelector = (pipelineId:number) => createSelector(
  pipelineConfigurationSelector(pipelineId),
  customActionSlotCountSelector(pipelineId),
  (pipeline, count) => {
    if(count === 0 || pipeline?.paused){
      return "default"
    }
    return pipeline?.customActionSlotRunning ? "processing" : "success";
  }
);

export const selectors = {
  rootSelector,
  isConnectedSelector,
  windowsSelector,
  configurationSelector,
  pipelineConfigurationSelector,
  hotkeysConfigurationSelector,
  customActionSlotsConfigurationSelector,
  isSelectedAndExistsSelector,
  customActionSlotCountSelector,
  customActionSlotStatusSelector
}



export const FlyffBotActions = configurationSlice.actions;

export const flyffBotReducer = configurationSlice.reducer;
