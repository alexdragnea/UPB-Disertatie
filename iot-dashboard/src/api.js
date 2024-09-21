import axios from 'axios';
import { useContext } from 'react';
import AuthContext from './AuthContext';

const API_URL = 'http://localhost:8888/v1/iot-user';

const api = axios.create({
    baseURL: API_URL,
    headers: { 'Content-Type': 'application/json' }
});

// Check if token is expired
const tokenIsExpired = (token) => {
    if (!token) return true;
    const payload = JSON.parse(atob(token.split('.')[1]));
    const now = Math.floor(Date.now() / 1000);
    return payload.exp < now;
};

// Add request interceptor
api.interceptors.request.use(
    async (config) => {
        const token = localStorage.getItem('accessToken');
        const { refreshToken } = useContext(AuthContext); // Use the AuthContext here

        if (tokenIsExpired(token)) {
            const newToken = await refreshToken(); // Fetch new token if expired
            config.headers.Authorization = `Bearer ${newToken}`;
        } else {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

export default api;
