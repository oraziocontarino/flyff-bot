
import { ApiEndpointBuilder } from "../core/api";
import { FlyffBotApiTag } from "../core/tagTypes";
import { HttpMethod } from "../types";

const BASE_URI = "/custom-action-slots";

export const addCustomActionSlot = (builder:ApiEndpointBuilder) =>
  builder.mutation<void, number>({
    // note: an optional `queryFn` may be used in place of `query`
    query: (pipelineId) => ({
      url: `${BASE_URI}/${pipelineId}`,
      method: HttpMethod.POST,
    }),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    invalidatesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  })

export const updateCustomActionSlotHexValue = (builder: ApiEndpointBuilder) =>
  builder.mutation<void, {customActionSlotId:number, hotkeyIndex:number, hexKeyCode:string}>({
    query: ({customActionSlotId, hotkeyIndex, hexKeyCode}) => ({
      url: `${BASE_URI}/${customActionSlotId}/key/${hotkeyIndex}`,
      method: HttpMethod.PUT,
      data: { hexKeyCode }
    }),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    invalidatesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  });

export const updateCustomActionSlotCastTime = (builder: ApiEndpointBuilder) =>
  builder.mutation<void, {customActionSlotId:number, castTimeMs:number}>({
    query: ({customActionSlotId, castTimeMs}) => ({
      url: `${BASE_URI}/${customActionSlotId}/cast-time`,
      method: HttpMethod.PUT,
      data: { castTimeMs }
    }),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    invalidatesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  });

export const deleteCustomActionSlot = (builder: ApiEndpointBuilder) =>
  builder.mutation<void, number>({
    query: (customActionSlotId) => ({
      url: `${BASE_URI}/${customActionSlotId}`,
      method: HttpMethod.DELETE
    }),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    invalidatesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  });
