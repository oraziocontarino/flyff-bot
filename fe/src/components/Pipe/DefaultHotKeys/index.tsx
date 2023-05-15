import { Descriptions } from "antd";
import { useTranslation } from "react-i18next";
import { FBCardTitle } from "../../common/CardTitle";
import { FBFeature } from "../../common/types";

const DefaultHotKeys: React.FC<FBFeature> = ({ pipelineId, i }) => {
  const { t } = useTranslation();
  return (
    <>
      <FBCardTitle title={t("pipe.defaultHotkeys.title")} />
      <Descriptions size={"small"}>
        <Descriptions.Item label={t("pipe.defaultHotkeys.pause")}>
          Shift + {i + (i + 1)}
        </Descriptions.Item>
        <Descriptions.Item label={t("pipe.defaultHotkeys.useCustomActionSlot")}>
          Shift + {i + (i + 2)}
        </Descriptions.Item>
      </Descriptions>
    </>
  );
};

export default DefaultHotKeys;
