
import api from '../core';
import { addHotkey, updateHotkeyHexValue, updateHotkeyDelay, updateHotkeyActive, deleteHotkey } from './endpoints';

export const hotkeyApi = api.injectEndpoints({
  endpoints: (builder) => ({
    addHotkey: addHotkey(builder),
    updateHotkeyHexValue: updateHotkeyHexValue(builder),
    updateHotkeyDelay: updateHotkeyDelay(builder),
    updateHotkeyActive: updateHotkeyActive(builder),
    deleteHotkey: deleteHotkey(builder),
  }),
});
