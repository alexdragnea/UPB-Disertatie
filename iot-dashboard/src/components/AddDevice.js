import React, { useState } from 'react';
import { TextField, Button, Paper, Grid, Snackbar, Alert } from '@mui/material';

export default function AddDevice() {
    const [deviceName, setDeviceName] = useState('');
    const [deviceType, setDeviceType] = useState('');
    const [description, setDescription] = useState('');
    const [location, setLocation] = useState('');
    const [measurementUnit, setMeasurementUnit] = useState('');

    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [validationErrors, setValidationErrors] = useState({});

    const validateForm = () => {
        let errors = {};

        if (!deviceName) {
            errors.deviceName = 'Device Name is required';
        }

        if (!deviceType) {
            errors.deviceType = 'Device Type is required';
        }

        if (!description) {
            errors.description = 'Description is required';
        }

        if (!location) {
            errors.location = 'Location is required';
        }

        if (!measurementUnit) {
            errors.measurementUnit = 'Measurement Unit is required';
        }

        return errors;
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        const errors = validateForm();
        if (Object.keys(errors).length > 0) {
            setValidationErrors(errors);
            return;
        }

        // Clear validation errors if form is valid
        setValidationErrors({});

        // Construct the payload that matches the DTO (DeviceRequestDto)
        const deviceData = {
            sensorName: deviceName,
            userId: deviceType,
            description: description,
            location: location,
            unit: measurementUnit,
        };

        // Send a POST request to the backend
        fetch('http://localhost:8888/v1/iot-core/device', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(deviceData),
        })
        .then((response) => {
            if (response.status === 201) {
                // Successfully added
                setSuccessMessage('Device added successfully!');
                setDeviceName('');
                setDeviceType('');
                setDescription('');
                setLocation('');
                setMeasurementUnit('');
            } else {
                return response.json().then((data) => {
                    throw new Error(data.message || 'Failed to add device');
                });
            }
        })
        .catch((error) => {
            setErrorMessage(error.message);
        });
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
                            error={!!validationErrors.deviceName}
                            helperText={validationErrors.deviceName}
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
                            error={!!validationErrors.deviceType}
                            helperText={validationErrors.deviceType}
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
                            error={!!validationErrors.description}
                            helperText={validationErrors.description}
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
                            error={!!validationErrors.location}
                            helperText={validationErrors.location}
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            label="Measurement Unit"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={measurementUnit}
                            onChange={(e) => setMeasurementUnit(e.target.value)}
                            error={!!validationErrors.measurementUnit}
                            helperText={validationErrors.measurementUnit}
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

            {/* Success Notification */}
            <Snackbar open={!!successMessage} autoHideDuration={6000} onClose={() => setSuccessMessage('')}>
                <Alert onClose={() => setSuccessMessage('')} severity="success" sx={{ width: '100%' }}>
                    {successMessage}
                </Alert>
            </Snackbar>

            {/* Error Notification */}
            <Snackbar open={!!errorMessage} autoHideDuration={6000} onClose={() => setErrorMessage('')}>
                <Alert onClose={() => setErrorMessage('')} severity="error" sx={{ width: '100%' }}>
                    {errorMessage}
                </Alert>
            </Snackbar>
        </Paper>
    );
}
