import { Descriptions } from "antd";
import { useTranslation } from "react-i18next";
import { FBCardTitle } from "../../common/CardTitle";
import {
  useCustomActionSlotLabel,
  usePausePipeShortcutLabel,
} from "../../common/hooks";
import { FBFeature } from "../../common/types";

const DefaultHotKeys: React.FC<FBFeature> = ({ i }) => {
  const { t } = useTranslation();

  const pauseShortcutLabel = usePausePipeShortcutLabel(i);

  const customActionSlotLabel = useCustomActionSlotLabel(i);

  return (
    <>
      <FBCardTitle title={t("pipe.defaultHotkeys.title")} />
      <Descriptions size={"small"}>
        <Descriptions.Item label={t("pipe.defaultHotkeys.pause")}>
          {pauseShortcutLabel}
        </Descriptions.Item>
        <Descriptions.Item label={t("pipe.defaultHotkeys.useCustomActionSlot")}>
          {customActionSlotLabel}
        </Descriptions.Item>
      </Descriptions>
    </>
  );
};

export default DefaultHotKeys;
