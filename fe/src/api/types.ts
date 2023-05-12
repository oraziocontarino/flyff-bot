export type RTKResponse<T> = {
  data: T,
  error: string,
  id: string,
  isError: boolean,
  isLoading: boolean
}

export type Configuration = {
  pipeline: Pipeline,
  hotkeys: Hotkey[];
  customActionSlots:CustomActionSlot[];
}

export type Pipeline = {
  id: number;
  selectedWindowHwnd: string;
  selectedWindowName: string;
  paused: boolean;
  customActionSlotRunning: boolean;
}

export type Hotkey = {
  id:number,
  active:boolean,
  delayMs:number,
  hexKeyCode0:AuxCodes,
  hexKeyCode1:KeyCodes,
  lastTimeExecuted:number
}

export enum AuxCodes {
  CTRL = '0x11',
  ALT = '0x12'
}

export enum KeyCodes {
  ONE = '0x31',
  TWO = '0x32',
  THREE = '0x33',
  FOUR = '0x34',
  FIVE = '0x35',
  SIX = '0x36',
  SEVEN = '0x37',
  EIGHT = '0x38',
  NINE = '0x39',
  ZERO = '0x30',
}

type CustomActionSlot = {
  id:number,
  castTime:number,
  hexKeyCode0:string,
  hexKeyCode1:string,
}

export enum HttpMethod {
  GET = 'GET',
  POST = 'POST',
  PUT = 'PUT',
  DELETE = 'DELETE'
}

export type WindowItem = {
  title: string;
  hwnd: string;
  isNull: boolean;
}

export type UpdateSelectedWindowRequest = {
  configurationId: number;
  hwnd?: string;
}
