// src/components/DeviceDetails.js
import React from 'react';
import { useParams } from 'react-router-dom';
import { Paper } from '@mui/material';

export default function DeviceDetails({ devices }) {
    const { id } = useParams();
    const device = devices.find(d => d.id === id);

    return (
        <Paper style={{ padding: 20 }}>
            <h2>Device Details</h2>
            {device ? (
                <div>
                    <p><strong>Name:</strong> {device.name}</p>
                    <p><strong>Type:</strong> {device.type}</p>
                    {/* Add more details as needed */}
                </div>
            ) : (
                <p>Device not found</p>
            )}
        </Paper>
    );
}
