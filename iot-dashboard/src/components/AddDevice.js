// src/components/AddDevice.js
import React, { useState } from 'react';
import { TextField, Button, Paper } from '@mui/material';

export default function AddDevice() {
    const [deviceName, setDeviceName] = useState('');
    const [deviceType, setDeviceType] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        // Handle device addition logic here
        console.log(`Device Added: ${deviceName} (${deviceType})`);
    };

    return (
        <Paper style={{ padding: 20 }}>
            <h2>Add New Device</h2>
            <form onSubmit={handleSubmit}>
                <TextField
                    label="Device Name"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    value={deviceName}
                    onChange={(e) => setDeviceName(e.target.value)}
                />
                <TextField
                    label="Device Type"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    value={deviceType}
                    onChange={(e) => setDeviceType(e.target.value)}
                />
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    style={{ marginTop: 20 }}
                >
                    Add Device
                </Button>
            </form>
        </Paper>
    );
}
