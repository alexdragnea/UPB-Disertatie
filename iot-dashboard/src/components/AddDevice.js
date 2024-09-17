import React, { useState } from 'react';
import { TextField, Button, Paper, Grid } from '@mui/material';

export default function AddDevice() {
    const [deviceName, setDeviceName] = useState('');
    const [deviceType, setDeviceType] = useState('');
    const [description, setDescription] = useState('');
    const [location, setLocation] = useState('');
    const [rangeValue, setRangeValue] = useState('');
    const [measurementUnit, setMeasurementUnit] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        // Handle device addition logic here
        console.log(`Device Added: 
            Name: ${deviceName}, 
            Type: ${deviceType}, 
            Description: ${description}, 
            Location: ${location}, 
            Range: ${rangeValue} ${measurementUnit}`);
    };

    return (
        <Paper style={{ padding: 20 }}>
            <h2>Add New Device</h2>
            <form onSubmit={handleSubmit}>
                <Grid container spacing={2}>
                    <Grid item xs={12} md={6}>
                        <TextField
                            label="Device Name"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={deviceName}
                            onChange={(e) => setDeviceName(e.target.value)}
                        />
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <TextField
                            label="Device Type"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={deviceType}
                            onChange={(e) => setDeviceType(e.target.value)}
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            label="Description"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            label="Location"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={location}
                            onChange={(e) => setLocation(e.target.value)}
                        />
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <TextField
                            label="Range Value"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={rangeValue}
                            onChange={(e) => setRangeValue(e.target.value)}
                        />
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <TextField
                            label="Measurement Unit"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={measurementUnit}
                            onChange={(e) => setMeasurementUnit(e.target.value)}
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <Button
                            type="submit"
                            variant="contained"
                            color="primary"
                            style={{ marginTop: 20 }}
                        >
                            Add Device
                        </Button>
                    </Grid>
                </Grid>
            </form>
        </Paper>
    );
}
