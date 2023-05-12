import { ApiEndpointBuilder } from "../core/api";
import { FlyffBotApiTag } from "../core/tagTypes";
import { HttpMethod } from "../types";

const BASE_URI = "/hotkeys"

export const addHotkey = (builder:ApiEndpointBuilder) =>
  builder.mutation<void, number>({
    // note: an optional `queryFn` may be used in place of `query`
    query: (pipelineId) => ({
      url: `${BASE_URI}/${pipelineId}`,
      method: HttpMethod.POST,
    }),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    invalidatesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  })

export const updateHotkeyHexValue = (builder: ApiEndpointBuilder) =>
  builder.mutation<void, {hotkeyId:number, hotkeyIndex:number, hexKeyCode:string}>({
    query: ({hotkeyId, hotkeyIndex, hexKeyCode}) => ({
      url: `${BASE_URI}/${hotkeyId}/key/${hotkeyIndex}`,
      method: HttpMethod.PUT,
      data: { hexKeyCode }
    }),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    invalidatesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  });

export const updateHotkeyDelay = (builder: ApiEndpointBuilder) =>
  builder.mutation<void, {hotkeyId:number, delayMs?:number}>({
    query: ({hotkeyId, delayMs}) => ({
      url: `${BASE_URI}/${hotkeyId}/delay`,
      method: HttpMethod.PUT,
      data: { delayMs }
    }),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    invalidatesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  });

export const updateHotkeyActive = (builder: ApiEndpointBuilder) =>
  builder.mutation<void, {hotkeyId:number, active:boolean}>({
    query: ({hotkeyId, active}) => ({
      url: `${BASE_URI}/${hotkeyId}/active`,
      method: HttpMethod.PUT,
      data: { active }
    }),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    invalidatesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  });

export const deleteHotkey = (builder: ApiEndpointBuilder) =>
  builder.mutation<void, number>({
    query: (hotkeyId) => ({
      url: `/hotkeys/${hotkeyId}`,
      method: HttpMethod.DELETE
    }),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    invalidatesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  });
