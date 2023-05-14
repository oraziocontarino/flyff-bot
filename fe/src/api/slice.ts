import { createSelector, createSlice } from '@reduxjs/toolkit';
import { Configuration, WindowItem } from '../api/types';
import { RootState } from '../store';

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
      state.configurations = args.payload;
    },
    storeWindowList: (state, args:{type:string, payload:WindowItem[]}) => {
      state.windows = args.payload;
    },
  },
});

const rootSelector = (state: RootState) => state.flyffBot;

const isConnectedSelector = createSelector(rootSelector, (rootSelector) => (
  rootSelector.isConnected
));

const windowsSelector = createSelector(rootSelector, (rootSelector) => (
  rootSelector.windows
));

const configurationSelector = (pipelineId:number) =>  createSelector(rootSelector, (rootSelector) => (
  rootSelector.configurations.find(item => item.pipeline.id === pipelineId)
));

const pipelineConfigurationSelector = (pipelineId:number) => createSelector(rootSelector, (rootSelector) => (
  rootSelector.configurations.find(item => item.pipeline.id === pipelineId)?.pipeline
));

const hotkeysConfigurationSelector = (pipelineId:number) => createSelector(rootSelector, (rootSelector) => (
  rootSelector.configurations.find(item => item.pipeline.id === pipelineId)?.hotkeys
));

const customActionSlotsConfigurationSelector = (pipelineId:number) => createSelector(rootSelector, (rootSelector) => (
  rootSelector.configurations.find(item => item.pipeline.id === pipelineId)?.customActionSlots
));

const isSelectedAndNotExistsSelector = (pipelineId:number) => createSelector(
  pipelineConfigurationSelector(pipelineId),
  windowsSelector,
  (pipelineSelector, windowsSelector) => (
    pipelineSelector?.selectedWindowHwnd && !windowsSelector.some(item => item.hwnd === pipelineSelector?.selectedWindowHwnd)
  )
);

const customActionSlotCountSelector = (pipelineId:number) => createSelector(
  customActionSlotsConfigurationSelector(pipelineId),
  (customActionSlotSelector) => customActionSlotSelector?.length ?? 0
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
  isSelectedAndNotExistsSelector,
  customActionSlotCountSelector,
  customActionSlotStatusSelector
}



export const FlyffBotActions = configurationSlice.actions;

export const flyffBotReducer = configurationSlice.reducer;
