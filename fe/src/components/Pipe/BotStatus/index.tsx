import { Descriptions } from "antd";
import { useTranslation } from "react-i18next";
import FBCardTitle from "../../common/CardTitle";
import { useSelectConfig } from "../../common/hooks";
import FBSpinner from "../../common/FBSpinner";
import { FBFeature } from "../../common/types";
import { useMemo } from "react";


const BotStatus:React.FC<FBFeature> = ({configurationId}) => {
  const {t} = useTranslation();
  const { pipeData } = useSelectConfig(configurationId);

  const isPaused = useMemo(() => (
    pipeData?.pipeline.paused
  ), [pipeData?.pipeline.paused]);

  const isAnyActive = useMemo(() => (
    pipeData?.hotkeys.find(item => item.active) !== undefined
  ), [pipeData?.hotkeys]);


  return (
    <>
      <FBCardTitle title={t('pipe.botStatus.title')}/>
      <Descriptions size={'small'}>
        <Descriptions.Item label={t('pipe.botStatus.hotKeysLoop.title')}>
          <FBSpinner paused={isPaused || !isAnyActive} />
        </Descriptions.Item>
      </Descriptions>
      <Descriptions size={'small'}>
        <Descriptions.Item label={t('pipe.botStatus.customActionSlot.title')}>
          <FBSpinner paused={isPaused || pipeData?.pipeline.customActionSlotRunning} />
        </Descriptions.Item>
      </Descriptions>
    </>
  );
}

export default BotStatus;

