import { EndpointBuilder } from '@reduxjs/toolkit/dist/query/endpointDefinitions';
import api from '.';

import { AxiosBaseQueryFn } from './axios';
import QueryTagTypes from './tagTypes';

export type ApiEndpointBuilder = EndpointBuilder<
  AxiosBaseQueryFn,
  QueryTagTypes,
  typeof api.reducerPath
>;
