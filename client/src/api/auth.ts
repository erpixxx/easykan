import axios from "axios"
import { env } from "../config/env.ts";
import type { User } from "../types/User.ts";
import type { AuthLoginRequestDto } from "../types/AuthLoginRequestDto.ts";
import api from "./axiosInstance.ts";

const API_URL = env.API_URL;

axios.defaults.withCredentials = true;

export const login = async (payload: AuthLoginRequestDto): Promise<User> => {
    const res = await axios.post(`${API_URL}/auth/login`, payload);
    return res.data;
}

export const logout = async (): Promise<void> => {
    await axios.post(`${API_URL}/logout`);
};

export const logoutAll = async (): Promise<void> => {
    await axios.post(`${API_URL}/logout-all`);
};

export const refreshToken = async (): Promise<void> => {
    await axios.post(`${API_URL}/refresh`);
};

export const getCurrentUser = async (): Promise<User | null> => {
    try {
        const res = await api.get(`${API_URL}/users/@me`);
        return res.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            if (error.code === 'ECONNABORTED') {
                return null;
            }
            if (error.response?.status === 401 || error.response?.status === 403) {
                return null;
            }
        }
        console.error('An unexpected error occurred while fetching user:', error);
        return null;
    }
}