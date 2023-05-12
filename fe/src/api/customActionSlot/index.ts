
import api from '../core';
import { updateHotkeyActive } from '../hotkey/endpoints';
import { addCustomActionSlot, updateCustomActionSlotCastTime, updateCustomActionSlotHexValue } from './endpoints';

export const customActionSlotApi = api.injectEndpoints({
  endpoints: (builder) => ({
    addCustomActionSlot: addCustomActionSlot(builder),
    updateCustomActionSlotHexValue: updateCustomActionSlotHexValue(builder),
    updateCustomActionSlotCastTime: updateCustomActionSlotCastTime(builder),
    deleteCustomActionSlot: updateHotkeyActive(builder),
  }),
});
