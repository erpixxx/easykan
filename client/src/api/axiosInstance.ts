import axios from "axios";
import { env } from "../config/env.ts";

const API_URL = env.API_URL;
const api = axios.create({
  baseURL: API_URL,
  withCredentials: true
})

api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    if (
      error.response !== undefined &&
      error.response.status === 401 &&
      originalRequest.url !== '/auth/refresh' &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;

      try {
        await axios.post(`${API_URL}/auth/refresh`, {}, { withCredentials: true });

        return api(originalRequest);
      } catch (refreshError) {
        if (window.location.pathname !== '/login') {
          window.location.href = '/login';
        }
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;