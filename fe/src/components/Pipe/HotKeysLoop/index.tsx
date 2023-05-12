import { Button, Col, InputNumber, Row, Select, Switch, Table } from "antd";
import { ColumnsType } from "antd/es/table";
import { useCallback, useMemo } from "react";
import { useTranslation } from "react-i18next";
import FBCardTitle from "../../common/CardTitle";
import {
  PlusCircleOutlined,
  DeleteOutlined
} from '@ant-design/icons';
import { FBFeature } from "../../common/types";
import { useSelectConfig } from "../../common/hooks";
import { AuxCodes, Hotkey, KeyCodes } from "../../../api/types";
import { useAddHotkeyMutation, useDeleteHotkeyMutation, useUpdateHotkeyActiveMutation, useUpdateHotkeyDelayMutation, useUpdateHotkeyHexValueMutation } from "../../../api/hotkey/hooks";
import { InputColum } from "./InputColumn";

const HotKeysLoop:React.FC<FBFeature> = ({configurationId}) => {
  const {t} = useTranslation();
  const { pipeData } = useSelectConfig(configurationId);
  const hotkeys = useMemo(()=> pipeData?.hotkeys ?? [], [pipeData?.hotkeys]);
  const [addHotkey] = useAddHotkeyMutation();
  const [updateDelay] = useUpdateHotkeyDelayMutation()
  const [updateActive] = useUpdateHotkeyActiveMutation();
  const [deleteHotkey] = useDeleteHotkeyMutation();





  const columns = useMemo<ColumnsType<Hotkey>>(()=> [
    {
      title: t('pipe.hotKeysLoop.keys.title'),
      key: 'keys',
      render: (_, item) => <InputColum item={item} />,
    },
    {
      title: t('pipe.hotKeysLoop.delayMs'),
      key: 'delayMs',
      width: 135,
      render: (_, item) => (
        <InputNumber
          value={item.delayMs}
          addonAfter={'ms'}
          onChange={(delayMs)=>updateDelay({hotkeyId: item.id, delayMs: delayMs ?? undefined })}
        />
      ),
    },
    {
      title: t('pipe.hotKeysLoop.active'),
      key: 'active',
      width: 75,
      render: (_, item) => (
        <Switch
          defaultChecked={item.active}
          onChange={(active) => updateActive({hotkeyId: item.id, active})}
        />
      ),
    },
    {
      title: <Button
        type="primary"
        icon={<PlusCircleOutlined />}
        onClick={()=>addHotkey(configurationId)}
      />,
      key: 'actions',
      width: 50,
      render: (_, item) => (
        <Button
          type="text"
          icon={<DeleteOutlined />}
          danger
          onClick={()=>deleteHotkey(item.id)}
        />
      ),
    },

  ], [addHotkey, configurationId, deleteHotkey, t, updateActive, updateDelay]);

  return (
    <>
      <FBCardTitle title={t('pipe.hotKeysLoop.title')}/>
      <Table
        columns={columns}
        dataSource={hotkeys}
        size="small"
        pagination={false}
      />
    </>
  );
}

export default HotKeysLoop;

