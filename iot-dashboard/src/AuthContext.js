import React, { createContext, useState, useEffect } from 'react';
import api from './api'; 

export const AuthContext = createContext(); // Export AuthContext

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true); // Add loading state
    const [refreshTimeout, setRefreshTimeout] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const now = Date.now() / 1000; // Current time in seconds

            // Check if token is expired
            if (payload.exp < now) {
                logout(); // Log out the user
            } else {
                // Token is valid
                setUser(payload);
                setIsAuthenticated(true);
                scheduleRefresh(token); // Schedule token refresh
            }
        } else {
            setIsAuthenticated(false);
            setUser(null);
        }
        setLoading(false); // Set loading to false after checking
    }, []);

    const scheduleRefresh = (token) => {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const expiryTime = payload.exp * 1000; // Convert to milliseconds
        const now = Date.now();

        // Calculate time until the token expires
        const timeUntilExpiry = expiryTime - now;

        // Schedule token refresh 1 minute before expiry
        if (timeUntilExpiry > 0) {
            const timeout = setTimeout(() => {
                refreshToken();
            }, timeUntilExpiry - 60000); // 60 seconds before expiry
            setRefreshTimeout(timeout);
        }
    };

    const login = (token) => {
        localStorage.setItem('accessToken', token);
        const payload = JSON.parse(atob(token.split('.')[1]));
        setUser(payload);
        setIsAuthenticated(true);
        scheduleRefresh(token); // Schedule token refresh on login
    };

    const logout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setIsAuthenticated(false);
        setUser(null);
        if (refreshTimeout) {
            clearTimeout(refreshTimeout); // Clear scheduled refresh
        }
    };

    const refreshToken = async () => {
        const storedRefreshToken = localStorage.getItem('refreshToken');
        if (!storedRefreshToken) {
            logout(); // Log out if no refresh token is available
            return null;
        }

        try {
            const response = await api.get('/token/refresh', {
                headers: { 'refresh-token': storedRefreshToken }
            });

            const { accessToken } = response.data;
            localStorage.setItem('accessToken', accessToken);
            const payload = JSON.parse(atob(accessToken.split('.')[1]));
            setUser(payload); // Update user info
            scheduleRefresh(accessToken); // Reschedule token refresh
            return accessToken;
        } catch (error) {
            console.error('Error refreshing token', error);
            logout(); // Log out if refresh fails
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
