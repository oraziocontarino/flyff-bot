import { Badge, Card } from "antd";
import { useMemo } from "react";
import { useTranslation } from "react-i18next";
import FBCardTitle from "../common/CardTitle";
import BotStatus from "./BotStatus";
import HotKeysLoop from "./HotKeysLoop";
import DefaultHotKeys from "./DefaultHotKeys";
import SelectWindowName from "./SelectWindowName";
import CustomActionSlot from "./CustomActionSlot";
import {
  PauseCircleOutlined,
} from '@ant-design/icons';

interface PipeProps {
  id:number
}

const Pipe:React.FC<PipeProps> = ({id}) => {
  const {t} = useTranslation();

  const configs = useMemo(()=>[
    { class: 'fb-card-grid-item-100', component: <FBCardTitle title={t('pipe.title', {id})}/> },
    { class: 'fb-card-grid-item-100', component: <SelectWindowName configurationId={id}/> },
    { class: 'fb-card-grid-item-50', component: <DefaultHotKeys configurationId={id}/> },
    { class: 'fb-card-grid-item-50', component: <BotStatus configurationId={id} /> },
    { class: 'fb-card-grid-item-100', component: <HotKeysLoop configurationId={id} /> },
    { class: 'fb-card-grid-item-100', component: <CustomActionSlot configurationId={id} /> },
  ], [id, t]);

  const content = useMemo(()=> configs.map((config, i) => (
    <Card.Grid
      key={`pipe-${id}-feature-${i}`}
      className={config.class}
      hoverable={false}
    >
      {config.component}
    </Card.Grid>
  )), [configs, id]);

  return (
    <Badge
      count={<PauseCircleOutlined/>}
      status={"processing"}
    >
      <Card className={"fb-card"}>
        {content}
      </Card>
    </Badge>
  );
}

export default Pipe;

