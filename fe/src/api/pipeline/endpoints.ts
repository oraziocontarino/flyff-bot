
import {Configuration, HttpMethod, Pipeline, UpdateSelectedWindowRequest, WindowItem } from "../types";
import { ApiEndpointBuilder } from "../core/api";
import { FlyffBotApiTag } from "../core/tagTypes";


export const fetchConfiguration = (builder:ApiEndpointBuilder) =>
  builder.query<Configuration[], void>({
    // note: an optional `queryFn` may be used in place of `query`
    query: (_body) => ({
      url: `configuration`,
      method: HttpMethod.GET,
    }),
    transformResponse: (response: Configuration[]) => response.map(item => ({
      ...item,
      pipeline: {
        ...item.pipeline,
        selectedWindowHwnd: item.pipeline.selectedWindowHwnd ?? undefined,
        selectedWindowName: item.pipeline.selectedWindowName ?? undefined,
      }
    })),
    transformErrorResponse: (response: { status: string | number }) => response.status,
    providesTags: (_result) => [{type: FlyffBotApiTag.CONFIGURATION}],
  })

export const fetchWindowList = (builder: ApiEndpointBuilder) =>
  builder.query<WindowItem[], void>({
    query: (_data) => ({
      url: '/windows',
      method: HttpMethod.GET,
    }),
    transformResponse: (response: WindowItem[]) => response,
    transformErrorResponse: (response: { status: string | number }) => response.status,
    providesTags: (_result) => [{type: FlyffBotApiTag.WINDOW_LIST}],
  });

export const updateSelectedWindow = (builder: ApiEndpointBuilder) =>
  builder.mutation<void, UpdateSelectedWindowRequest>({
    query: (data) => {
      return {
      url: '/windows',
      method: HttpMethod.PUT,
      data,
    }},
    invalidatesTags: (_response, _error) => [{ type: FlyffBotApiTag.CONFIGURATION }],
  });
