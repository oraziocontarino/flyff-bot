import { BaseQueryFn } from '@reduxjs/toolkit/dist/query';
import { AxiosRequestConfig } from 'axios';

export type AxiosBaseQueryFn = BaseQueryFn<AxiosRequestConfig>;

export default AxiosBaseQueryFn;
