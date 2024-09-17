import React, { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Header from './components/Header';

// Lazy load the components
const Dashboard = lazy(() => import('./components/Dashboard'));
const DeviceList = lazy(() => import('./components/DeviceList'));
const DeviceDetails = lazy(() => import('./components/DeviceDetails'));
const AddDevice = lazy(() => import('./components/AddDevice'));
const UserProfile = lazy(() => import('./components/UserProfile'));

function App() {
    const devices = [
        { id: '1', name: 'Sensor A', type: 'Temperature' },
        { id: '2', name: 'Sensor B', type: 'Humidity' },
    ];

    return (
        <Router>
            <Header />
            <div style={{ display: 'flex', marginTop: '60px' }}>
                <Sidebar devices={devices} /> {/* Passing devices as a prop */}
                <div style={{ flexGrow: 1, padding: '20px' }}>
                    <Suspense fallback={<div>Loading...</div>}>
                        <Routes>
                            <Route path="/admin/dashboard" element={<Dashboard />} />
                            <Route path="/admin/devices" element={<DeviceList devices={devices} />} />
                            <Route path="/admin/devices/:id" element={<DeviceDetails devices={devices} />} />
                            <Route path="/admin/add-device" element={<AddDevice />} />
                            <Route path="/admin/profile" element={<UserProfile />} />
                        </Routes>
                    </Suspense>
                </div>
            </div>
        </Router>
    );
}

export default App;
