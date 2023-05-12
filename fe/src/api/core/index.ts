import { createApi } from '@reduxjs/toolkit/query/react';
import {baseQueryFn} from './baseQueryFn';

const api = createApi({
  baseQuery: baseQueryFn,
  endpoints: () => ({}),
});

export default api;
