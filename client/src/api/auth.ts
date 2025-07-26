import axios from "axios"

axios.defaults.withCredentials = true;

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function login(email: string, password: string) {
    await axios.post(`${API_BASE_URL}/auth/login`, {email, password});
}

export async function logout() {
    await axios.post(`${API_BASE_URL}/auth/logout`);
}

export async function getCurrentUser() {
    const res = await axios.get(`${API_BASE_URL}/users/@me`)
    return res.data;
}