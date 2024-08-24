import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Dashboard from './components/Dashboard';
import DeviceList from './components/DeviceList';
import DeviceDetails from './components/DeviceDetails';
import AddDevice from './components/AddDevice';
import UserProfile from './components/UserProfile';

function App() {
    const devices = [
        { id: '1', name: 'Sensor A', type: 'Temperature' },
        { id: '2', name: 'Sensor B', type: 'Humidity' },
    ];

    return (
        <Router>
            <Header />
            <div style={{ display: 'flex', marginTop: '60px' }}>
                <Sidebar />
                <div style={{ flexGrow: 1, padding: '20px' }}>
                    <Routes>
                        <Route path="/admin/dashboard" element={<Dashboard />} />
                        <Route path="/admin/devices" element={<DeviceList devices={devices} />} />
                        <Route path="/admin/devices/:id" element={<DeviceDetails devices={devices} />} />
                        <Route path="/admin/add-device" element={<AddDevice />} />
                        <Route path="/admin/profile" element={<UserProfile />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
}

export default App;
