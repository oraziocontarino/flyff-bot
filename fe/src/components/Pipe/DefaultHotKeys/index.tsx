import { Descriptions } from "antd";
import { useTranslation } from "react-i18next";
import FBCardTitle from "../../common/CardTitle";
import { FBFeature } from "../../common/types";


const DefaultHotKeys:React.FC<FBFeature> = ({configurationId}) => {
  const {t} = useTranslation();
  return (
    <>
      <FBCardTitle title={t('pipe.defaultHotkeys.title')}/>
      <Descriptions size={'small'}>
        <Descriptions.Item label={t('pipe.defaultHotkeys.pause')}>
          Shift + 1
        </Descriptions.Item>
      </Descriptions>
      <Descriptions size={'small'}>
        <Descriptions.Item label={t('pipe.defaultHotkeys.useCustomActionSlot')}>Shift + 2</Descriptions.Item>
      </Descriptions>
    </>
  );
}

export default DefaultHotKeys;

