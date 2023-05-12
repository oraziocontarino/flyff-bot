import { Badge, Button, Col, Collapse, InputNumber, Row, Select, Table } from "antd";
import { ColumnsType } from "antd/es/table";
import { useCallback, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useSelectConfig } from "../../common/hooks";
import { FBFeature } from "../../common/types";
import {
  PlusCircleOutlined,
  DeleteOutlined
} from '@ant-design/icons';
import { AuxCodes, KeyCodes } from "../../../api/types";

interface DataType {
  key0: string;
  key1: string;
  delayMs: number;
}

const CustomActionSlot:React.FC<FBFeature> = ({configurationId}) => {
  const {t} = useTranslation();
  const { pipeData } = useSelectConfig(configurationId);
  const customActionSlots = useMemo(()=> pipeData?.customActionSlots ?? [], [pipeData?.customActionSlots]);

  const handleChange = useCallback(()=>{}, []);

  const prepareOptions = useCallback((enumOptions: typeof AuxCodes | typeof KeyCodes) => (
    Object.values(enumOptions).map(value => (
      {value, label: t(`common.keyCodes.${value}`)}
    ))
  ), [t]);

  const auxKeysOptions = useMemo(() => prepareOptions(AuxCodes), [prepareOptions]);
  const keyCodesOptions = useMemo(() => prepareOptions(KeyCodes), [prepareOptions]);

  const columns = useMemo<ColumnsType<DataType>>(()=> [
    {
      title: t('pipe.customActionSlot.keys.title'),
      key: 'keys',
      render: (item) => (
        <Row justify={'space-around'}>
          <Col span={10}>
          <Select
            className={'width-100'}
            value={item.key0}
            onChange={handleChange}
            options={[
              { value: '', label: t('pipe.customActionSlot.keys.placeholder'), disabled: true },
              ...auxKeysOptions
            ]}
          />
          </Col>
          <Col span={10}>
          <Select
            className={'width-100'}
            value={item.key1}
            onChange={handleChange}
            options={[
              { value: '', label: t('pipe.customActionSlot.keys.placeholder'), disabled: true },
              ...keyCodesOptions
            ]}
          />
          </Col>
        </Row>
      ),
    },
    {
      title: t('pipe.customActionSlot.castTimeMs'),
      key: 'delayMs',
      width: 210,
      render: (item) => <InputNumber value={item.delayMs} addonAfter={'ms'}/>,
    },
    {
      title: <Button type="primary" icon={<PlusCircleOutlined />} />,
      key: 'actions',
      width: 50,
      render: (_item) => <Button type="text" icon={<DeleteOutlined />} danger />,
    },

  ], [auxKeysOptions, handleChange, keyCodesOptions, t]);

  const data = useMemo<DataType[]>(() => (customActionSlots).map(row => ({
    key0: row.hexKeyCode0,
    key1: row.hexKeyCode1,
    delayMs: row.castTime,
  })), [customActionSlots]);

  const count = useMemo(() => (customActionSlots).length, [customActionSlots]);

  const status = useMemo(() => {
    if(!pipeData?.customActionSlots?.length || pipeData?.pipeline?.paused){
      return "default"
    }

    return pipeData?.pipeline?.customActionSlotRunning ? "processing" : "success";
  }, [pipeData?.customActionSlots?.length, pipeData?.pipeline?.customActionSlotRunning, pipeData?.pipeline?.paused]);

  return (
    <Badge count={count} status={status} className={"width-100"}>
      <Collapse ghost>
        <Collapse.Panel
          header={t('pipe.customActionSlot.title', {count})}
          key="1"
          className="fb-collapse-padding-0 fb-card-title"
        >
          <Table
            columns={columns}
            dataSource={data}
            size="small"
            pagination={false}
          />
        </Collapse.Panel>
      </Collapse>
    </Badge>
  );
}

export default CustomActionSlot;

