import { useCallback, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { Configuration, WindowItem } from "../../api/types";
import { selectors } from "../slice";
import { connect, sendMessage, subscribe, SendTopic, ReceiveTopic } from "../socket";
import { UpdateSelectedWindowRequestDto } from "./types";

export const useSocketConnect = () => {
  const [isConnected, setIsConnected] = useState(false);

  useEffect(()=>{
    if(isConnected){
      return;
    }
    connect().then(setIsConnected);
  }, [isConnected]);

  return { isConnected };
}

export const useRequestConfiguration = () => {

  const requestConfiguration = useCallback(()=>{
    setTimeout(()=>{
      console.log("@@@ requestConfiguration...");
      sendMessage(SendTopic.GET_CONFIGURATIONS);
    }, 1000)
  }, []);

  return { requestConfiguration };
}

export const useSubribeConfigurationUpdates = () => {
  const isConnected = useSelector(selectors.isConnectedSelector);
  const [configurations, setConfigurations] = useState<Configuration[]>([]);

  useEffect(()=>{
    if(!isConnected){
      return;
    }
    subscribe(ReceiveTopic.UPDATED_CONFIGURATIONS_TOPIC, (response:Configuration[]) => {
      setConfigurations(response);
    });
  }, [isConnected]);

  return { configurations };
}

export const useSubribeWindowListUpdates = () => {
  const isConnected = useSelector(selectors.isConnectedSelector);
  const [windowList, setWindowList] = useState<WindowItem[]>([]);

  useEffect(()=>{
    if(!isConnected){
      return;
    }
    subscribe(ReceiveTopic.UPDATED_WINDOW_LIST, (response:WindowItem[]) => {
      setWindowList(response);
    });
  }, [isConnected]);

  return { windowList };
}

export const useUpdateSelectedWindow = () => {
  const updateSelectedWindow = useCallback((request:UpdateSelectedWindowRequestDto)=>{
    sendMessage(SendTopic.PUT_SELECTED_WINDOW, request);
  }, []);

  return { updateSelectedWindow };
}
