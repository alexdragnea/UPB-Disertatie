// authContext.js
import React, { createContext, useState, useEffect } from 'react';
import api from './api'; 

export const AuthContext = createContext(); // Export AuthContext

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            const payload = JSON.parse(atob(token.split('.')[1])); // Decode JWT
            setUser(payload);
            setIsAuthenticated(true);
        } else {
            setIsAuthenticated(false);
            setUser(null);
        }
    }, []);

    const login = (token) => {
        localStorage.setItem('accessToken', token);
        const payload = JSON.parse(atob(token.split('.')[1]));
        setUser(payload);
        setIsAuthenticated(true);
    };

    const logout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setIsAuthenticated(false);
        setUser(null);
    };

    const refreshToken = async () => {
        const storedRefreshToken = localStorage.getItem('refreshToken');
        if (!storedRefreshToken) return null;

        try {
            const response = await api.get('/token/refresh', {
                headers: { 'refresh-token': storedRefreshToken }
            });

            const { accessToken } = response.data;
            localStorage.setItem('accessToken', accessToken);
            const payload = JSON.parse(atob(accessToken.split('.')[1]));
            setUser(payload); // Update user info
            return accessToken;
        } catch (error) {
            console.error('Error refreshing token', error);
            logout(); // Log out if refresh fails
            return null;
        }
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, user, login, logout, refreshToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;
