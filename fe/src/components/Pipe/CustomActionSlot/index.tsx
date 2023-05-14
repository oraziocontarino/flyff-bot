import { Badge, Button, Col, Collapse, InputNumber, Row, Select, Table } from "antd";
import { ColumnsType } from "antd/es/table";
import { useCallback, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { FBFeature } from "../../common/types";
import {
  PlusCircleOutlined,
  DeleteOutlined
} from '@ant-design/icons';
import { AuxCodes, CustomActionSlotItem, KeyCodes } from "../../../api/types";
import { useSelector } from "react-redux";
import { selectors } from "../../../api/slice";
import {
  useAddCustomActionSlot,
  useUpdateCustomActionSlotHexValue,
  useUpdateCustomActionSlotCastTime,
  useDeleteCustomActionSlot
} from "../../../api/hooks/customActionSlots";

const CustomActionSlot:React.FC<FBFeature> = ({pipelineId}) => {
  const {t} = useTranslation();
  const customActionSlotsConfiguration = useSelector(selectors.customActionSlotsConfigurationSelector(pipelineId));
  const customActionSlotCount = useSelector(selectors.customActionSlotCountSelector(pipelineId));
  const customActionSlotStatus = useSelector(selectors.customActionSlotStatusSelector(pipelineId));

  const addCustomActionSlot = useAddCustomActionSlot();
  const updateCustomActionSlotHexValue = useUpdateCustomActionSlotHexValue()
  const updateCustomActionSlotCastTime = useUpdateCustomActionSlotCastTime();
  const deleteCustomActionSlot = useDeleteCustomActionSlot();

  const prepareOptions = useCallback((enumOptions: typeof AuxCodes | typeof KeyCodes) => (
    Object.values(enumOptions).map(value => (
      {value, label: t(`common.keyCodes.${value}`)}
    ))
  ), [t]);

  const auxKeysOptions = useMemo(() => prepareOptions(AuxCodes), [prepareOptions]);
  const keyCodesOptions = useMemo(() => prepareOptions(KeyCodes), [prepareOptions]);

  const columns = useMemo<ColumnsType<CustomActionSlotItem>>(()=> [
    {
      title: t('pipe.customActionSlot.keys.title'),
      key: 'keys',
      render: (_, item) => (
        <Row justify={'space-around'}>
          <Col span={10}>
            <Select
              className={'width-100'}
              value={item.hexKeyCode0}
              onChange={hexKeyCode => updateCustomActionSlotHexValue({id: item.id, keyIndex: 0, hexKeyCode})}
              options={[
                { value: '', label: t('pipe.customActionSlot.keys.placeholder'), disabled: true },
                ...auxKeysOptions
              ]}
            />
          </Col>
          <Col span={10}>
            <Select
              className={'width-100'}
              value={item.hexKeyCode1}
              onChange={hexKeyCode => updateCustomActionSlotHexValue({id: item.id, keyIndex: 1, hexKeyCode})}
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
      render: (_, item) => <InputNumber
        value={item.castTime}
        addonAfter={'ms'}
        onChange={(castTimeMs)=>updateCustomActionSlotCastTime({id: item.id, castTimeMs: castTimeMs ?? undefined })}
      />,
    },
    {
      title: (
        <Button
          type="primary"
          icon={<PlusCircleOutlined />}
          onClick={()=> addCustomActionSlot({pipelineId})}
        />
      ),
      key: 'actions',
      width: 50,
      render: (_, item) => (
        <Button
          type="text"
          icon={<DeleteOutlined />}
          danger
          onClick={()=> deleteCustomActionSlot({id: item.id})}
        />
      ),
    },
  ], [
    addCustomActionSlot,
    auxKeysOptions,
    deleteCustomActionSlot,
    keyCodesOptions,
    pipelineId,
    t,
    updateCustomActionSlotCastTime,
    updateCustomActionSlotHexValue
  ]);

  return (
    <Badge count={customActionSlotCount} status={customActionSlotStatus} className={"width-100"}>
      <Collapse ghost>
        <Collapse.Panel
          header={t('pipe.customActionSlot.title', {customActionSlotCount})}
          key="1"
          className="fb-collapse-padding-0 fb-card-title"
        >
          <Table
            columns={columns}
            dataSource={customActionSlotsConfiguration}
            size="small"
            pagination={false}
          />
        </Collapse.Panel>
      </Collapse>
    </Badge>
  );
}

export default CustomActionSlot;

