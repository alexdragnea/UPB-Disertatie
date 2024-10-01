import React, { Suspense, lazy, useContext } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import { AuthProvider, AuthContext } from './AuthContext'; // Import AuthContext
import PrivateRoute from './PrivateRoute'; // PrivateRoute component
import SensorDetail from './components/SensorDetail';

// Lazy loaded components
const Dashboard = lazy(() => import('./components/Dashboard'));
const UserProfile = lazy(() => import('./components/UserProfile'));
const LoginPage = lazy(() => import('./components/LoginPage'));
const RegisterPage = lazy(() => import('./components/RegisterPage'));
const ApiUsagePage = lazy(() => import('./components/ApiUsagePage'));

function App() {
    return (
        <AuthProvider>
            <Router>
                <AppWithAuth />
            </Router>
        </AuthProvider>
    );
}

const AppWithAuth = () => {
    const { isAuthenticated } = useContext(AuthContext); // Accessing AuthContext here

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
    };

    return (
        <>
            <Header onLogout={handleLogout} isAuthenticated={isAuthenticated} />
            <div style={{ display: 'flex', marginTop: '10px' }}>
                {isAuthenticated && <Sidebar />}
                <div style={{ flexGrow: 1, padding: '20px' }}>
                    <Suspense fallback={<div>Loading...</div>}>
                        <Routes>
                            <Route path="/" element={<PrivateRoute Component={Dashboard} />} />
                            <Route path="/sensor/:id" element={<PrivateRoute Component={SensorDetail} />} />
                            <Route path="/profile" element={<PrivateRoute Component={UserProfile} />} />
                            <Route path="/api-usage" element={<PrivateRoute Component={ApiUsagePage} />} />
                            <Route path="/login" element={isAuthenticated ? <Navigate to="/" /> : <LoginPage />} />
                            <Route path="/register" element={isAuthenticated ? <Navigate to="/" /> : <RegisterPage />} />
                            <Route path="*" element={<Navigate to="/" />} />
                        </Routes>
                    </Suspense>
                </div>
            </div>
        </>
    );
};

export default App;
