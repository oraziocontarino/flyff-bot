import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import AppBaseQueryFn from './axios';

export const baseQueryFn: AppBaseQueryFn = async (config: AxiosRequestConfig) => {
  try {
    // if (!process.env.REACT_APP_DEV_MODE && process.env.REACT_APP_API_URL)
    //   config.baseURL = process.env.REACT_APP_API_URL;

    config.headers = {
      'Content-Type': 'application/json',
    };

    const result = await axios(config);
    return { data: result.data };
  } catch (axiosError) {
    const error = axiosError as AxiosError;
    return {
      error,
    };
  }
};
