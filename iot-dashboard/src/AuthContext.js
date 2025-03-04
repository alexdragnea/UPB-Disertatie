import React, { createContext, useState, useEffect } from 'react';
import api from './api';

export const AuthContext = createContext(); // Export AuthContext

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [refreshTimeout, setRefreshTimeout] = useState(null);

    useEffect(() => {
        const token = sessionStorage.getItem('accessToken');
        if (token) {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const now = Date.now() / 1000;

            if (payload.exp < now) {
                logout();
            } else {
                setUser(payload);
                setIsAuthenticated(true);
                scheduleRefresh(token);
            }
        } else {
            setIsAuthenticated(false);
            setUser(null);
        }
        setLoading(false);
    }, []);

    const scheduleRefresh = (token) => {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const expiryTime = payload.exp * 1000;
        const now = Date.now();

        let timeUntilExpiry = expiryTime - now - 60000; // 60 seconds before expiry

        if (timeUntilExpiry < 0) {
            timeUntilExpiry = 5000; // Default to 5 seconds if already near expiry
        }

        if (timeUntilExpiry > 0) {
            const timeout = setTimeout(() => {
                refreshToken();
            }, timeUntilExpiry);
            setRefreshTimeout(timeout);
        }
    };

    const login = (token, refreshToken) => {
        sessionStorage.setItem('accessToken', token);
        sessionStorage.setItem('refreshToken', refreshToken);
        const payload = JSON.parse(atob(token.split('.')[1]));
        setUser(payload);
        setIsAuthenticated(true);
        scheduleRefresh(token);
    };

    const logout = () => {
        sessionStorage.removeItem('accessToken');
        sessionStorage.removeItem('refreshToken');
        setIsAuthenticated(false);
        setUser(null);
        if (refreshTimeout) {
            clearTimeout(refreshTimeout);
            setRefreshTimeout(null);
        }
    };

    const refreshToken = async () => {
        const storedRefreshToken = sessionStorage.getItem('refreshToken');
        if (!storedRefreshToken) {
            logout();
            return null;
        }

        try {
            const response = await api.get('/token/refresh', {
                headers: { 'refresh-token': storedRefreshToken }
            });

            const { accessToken, refreshToken } = response.data;
            sessionStorage.setItem('accessToken', accessToken);
            sessionStorage.setItem('refreshToken', refreshToken);
            const payload = JSON.parse(atob(accessToken.split('.')[1]));
            setUser(payload);
            scheduleRefresh(accessToken);
            return accessToken;
        } catch (error) {
            console.error('Error refreshing token', error);
            logout();
            return null;
        }
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, user, loading, login, logout, refreshToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;
