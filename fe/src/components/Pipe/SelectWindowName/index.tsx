import { Select } from "antd";
import { useEffect, useMemo, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { useUpdateSelectedWindowMutation } from "../../../api/pipeline/hooks";
import { windowListSelector } from "../../../api/pipeline/selectors";
import FBCardTitle from "../../common/CardTitle";
import { useSelectConfig } from "../../common/hooks";
import { FBFeature } from "../../common/types";


const SelectWindowName:React.FC<FBFeature> = ({configurationId}) => {
  const {t} = useTranslation();
  const { pipeData } = useSelectConfig(configurationId);
  const selectedWindowHwnd = useMemo(()=> pipeData?.pipeline.selectedWindowHwnd, [pipeData?.pipeline.selectedWindowHwnd]);

  const windowList = useSelector(windowListSelector);
  const [updateSelectedWindow] = useUpdateSelectedWindowMutation();

  const options = useMemo(() => (
    windowList?.map(({hwnd:value, title:label}) => ({value, label})) ?? []
  ), [windowList]);

  useEffect(() => {
    if(windowList === undefined || selectedWindowHwnd === undefined) {
      // Nothing to unselect, it's already undefined!
      return;
    }
    const notExists = !windowList.some(item => item.hwnd === selectedWindowHwnd);
    if(notExists){
      console.log("@@@ " + configurationId + " Case B] debug option pipeData ", {selectedWindowHwnd, pipeData, windowList});
      updateSelectedWindow({configurationId, hwnd: undefined});
    }
  }, [configurationId, pipeData, selectedWindowHwnd, updateSelectedWindow, windowList]);

  return (
    <>
    <FBCardTitle title={t('pipe.selectWindowName.title')}/>
    <Select
      placeholder={t('pipe.selectWindowName.placeholder')}
      value={selectedWindowHwnd}
      className={'width-100'}
      onChange={(hwnd) => {
        console.log("@@@ sending update...", {configurationId, hwnd});
        updateSelectedWindow({configurationId, hwnd});
      }}
      options={options}
    />
    </>
  );
}

export default SelectWindowName;
