import { Descriptions } from "antd";
import { useTranslation } from "react-i18next";
import FBCardTitle from "../../common/CardTitle";
import FBSpinner from "../../common/FBSpinner";
import { FBFeature } from "../../common/types";
import { useMemo } from "react";
import { useSelector } from "react-redux";
import { selectors } from "../../../api/slice";


const BotStatus:React.FC<FBFeature> = ({pipelineId}) => {
  const {t} = useTranslation();
  const pipelineConfiguration = useSelector(selectors.pipelineConfigurationSelector(pipelineId));
  const hotkeysConfiguration = useSelector(selectors.hotkeysConfigurationSelector(pipelineId));

  const isAnyActive = useMemo(() => (
    hotkeysConfiguration?.find(item => item.active) !== undefined
  ), [hotkeysConfiguration]);


  return (
    <>
      <FBCardTitle title={t('pipe.botStatus.title')}/>
      <Descriptions size={'small'}>
        <Descriptions.Item label={t('pipe.botStatus.hotKeysLoop.title')}>
          <FBSpinner paused={pipelineConfiguration?.paused || !isAnyActive} />
        </Descriptions.Item>
      </Descriptions>
      <Descriptions size={'small'}>
        <Descriptions.Item label={t('pipe.botStatus.customActionSlot.title')}>
          <FBSpinner paused={pipelineConfiguration?.paused || pipelineConfiguration?.customActionSlotRunning} />
        </Descriptions.Item>
      </Descriptions>
    </>
  );
}

export default BotStatus;

