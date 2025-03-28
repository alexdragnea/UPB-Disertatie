import React, { Suspense, lazy, useContext } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import { AuthProvider, AuthContext } from './AuthContext';
import PrivateRoute from './PrivateRoute';
import SensorDetail from './components/SensorDetail';

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
    const { isAuthenticated } = useContext(AuthContext);

    const handleLogout = () => {
        sessionStorage.removeItem('accessToken');
        sessionStorage.removeItem('refreshToken');
        window.location.href = '/login';
    };

    return (
        <>
            <Header onLogout={handleLogout} isAuthenticated={isAuthenticated} />
            <div style={{ display: 'flex', flexDirection: 'column', height: '100vh', overflow: 'hidden' }}>
                <div style={{ display: 'flex', flexGrow: 1, marginTop: '10px' }}>
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
            </div>
        </>
    );
};

export default App;