import React, { useState, useEffect } from 'react';
import {
    Paper,
    Typography,
    Button,
    TextField,
    Snackbar,
    Alert,
    Box,
    Tabs,
    Tab,
    Grid,
    Divider,
    IconButton,
    Container,
    CssBaseline,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
} from '@mui/material';
import { CopyAll, Refresh, DeviceHub } from '@mui/icons-material';

const ApiUsagePage = () => {
    const [bearerToken, setBearerToken] = useState('');
    const [userId, setUserId] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [tabValue, setTabValue] = useState(0);
    const [validationResult, setValidationResult] = useState('');
    const [openConfirm, setOpenConfirm] = useState(false);

    useEffect(() => {
        const fetchUserId = async () => {
            const token = localStorage.getItem('accessToken');
            if (token) {
                setBearerToken(token);
                try {
                    const response = await fetch('http://localhost:8888/v1/iot-user/logged', {
                        method: 'GET',
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                    if (!response.ok) throw new Error('Failed to fetch user details');
                    const data = await response.json();
                    setUserId(data.userId);
                    setMessage('User ID fetched successfully!');
                } catch (err) {
                    setError(err.message);
                }
            } else {
                setError('No Bearer token found. Please log in first.');
            }
        };

        fetchUserId();
    }, []);

    const fetchBearerToken = () => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            setBearerToken(token);
            setMessage('Bearer token fetched successfully!');
        } else {
            setError('No Bearer token found. Please log in first.');
        }
    };

    const handleCopyToken = () => {
        navigator.clipboard.writeText(bearerToken);
        setMessage('Bearer token copied to clipboard!');
    };

    const handleOpenConfirm = () => setOpenConfirm(true);
    const handleCloseConfirm = () => setOpenConfirm(false);

    const handleRefreshToken = () => {
        const refreshToken = sessionStorage.getItem('refreshToken');
        fetch('http://localhost:8888/v1/iot-user/token/refresh', {
            method: 'GET',
            headers: {
                'refresh-token': refreshToken,
            },
        })
        .then(response => {
            if (!response.ok) throw new Error('Failed to refresh token');
            return response.json();
        })
        .then(data => {
            setBearerToken(data.newToken);
            setMessage('Token refreshed successfully!');
            handleCloseConfirm();
        })
        .catch(err => {
            setError(err.message);
            handleCloseConfirm();
        });
    };

    const handleValidateToken = () => {
        fetch(`http://localhost:8888/v1/iot-user/validateToken?token=${bearerToken}`, {
            method: 'POST',
        })
        .then(response => {
            if (!response.ok) throw new Error('Token validation failed');
            return response.json();
        })
        .then(data => {
            setMessage('Token validated successfully!');
        })
        .catch(err => setError(err.message));
    };

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
    };

    const renderCommandExample = () => {
        const userIdString = userId || 'YOUR_USER_ID'; // Fallback if userId is not fetched
        switch (tabValue) {
            case 0:
                return (
                    <pre style={{ backgroundColor: '#f8f8f8', padding: 10, borderRadius: 5 }}>
                        {`curl --location 'http://localhost:8888/v1/iot-bridge' \\
--header 'Authorization: Bearer ${bearerToken}' \\
--header 'Content-Type: application/json' \\
--data '{
    "measurement": "temperature",
    "sensorName": "living_room_sensor",
    "userId": "${userIdString}",
    "value": 22.5,
    "unit": "C"
}'`}
                    </pre>
                );
            case 1:
                return (
                    <pre style={{ backgroundColor: '#f8f8f8', padding: 10, borderRadius: 5 }}>
                        {`POST http://localhost:8888/v1/iot-bridge
Headers:
Authorization: Bearer ${bearerToken}
Content-Type: application/json

Body:
{
    "measurement": "humidity",
    "sensorName": "bathroom_sensor",
    "userId": "${userIdString}",
    "value": 60,
    "unit": "%"
}`}
                    </pre>
                );
            case 2:
                return (
                    <pre style={{ backgroundColor: '#f8f8f8', padding: 10, borderRadius: 5 }}>
                        {`fetch('http://localhost:8888/v1/iot-bridge', {
    method: 'POST',
    headers: {
        'Authorization': 'Bearer ${bearerToken}',
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        measurement: "light",
        sensorName: "garden_sensor",
        userId: "${userIdString}",
        value: 300,
        unit: "lx"
    })
})
.then(response => response.json())
.then(data => console.log(data));`}
                    </pre>
                );
            default:
                return null;
        }
    };

    const handleCopyCommand = () => {
        const command = renderCommandExample();
        const commandText = command.props.children;
        navigator.clipboard.writeText(commandText);
        setMessage('Command copied to clipboard!');
    };

    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                minHeight: '100vh',
                backgroundColor: '#0000',
                paddingY: 1,
                width: '100%',
                marginTop: -6,
            }}
        >
            <CssBaseline />

            <Container sx={{ mt: 2, mb: 2, width: '100%', maxWidth: 'xl', paddingX: { xs: 2, sm: 4 } }}>
                <Paper sx={{ padding: { xs: 2, sm: 3 }, borderRadius: 2, boxShadow: 3 }}>
                    <Typography variant="h4" gutterBottom>
                        <DeviceHub sx={{ marginRight: 1 }} /> API Usage Overview
                    </Typography>
                    <Divider sx={{ marginBottom: 2 }} />

                    <Tabs value={tabValue} onChange={handleTabChange} indicatorColor="primary" textColor="primary">
                        <Tab label="cURL" />
                        <Tab label="Postman" />
                        <Tab label="JavaScript" />
                    </Tabs>

                    <Box mt={2} sx={{ position: 'relative' }}>
                        <IconButton
                            sx={{ position: 'absolute', right: 0, top: 0 }}
                            onClick={handleCopyCommand}
                        >
                            <CopyAll />
                        </IconButton>
                        {renderCommandExample()}
                    </Box>

                    <Typography variant="body1" paragraph>
                        Replace the placeholder values with your actual data. You can send measurements such as temperature, humidity, light intensity, etc.
                    </Typography>

                    <Grid container spacing={2} mt={2}>
                        <Grid item xs={12}>
                            <Typography variant="h6" gutterBottom>
                                Fetching Your Bearer Token
                            </Typography>
                            <Button variant="contained" color="primary" onClick={fetchBearerToken}>
                                Fetch Bearer Token
                            </Button>
                        </Grid>

                        {bearerToken && (
                            <>
                                <Grid item xs={12}>
                                    <TextField
                                        label="Bearer Token"
                                        value={bearerToken}
                                        variant="outlined"
                                        fullWidth
                                        InputProps={{
                                            readOnly: true,
                                        }}
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <Button variant="outlined" startIcon={<CopyAll />} onClick={handleCopyToken}>
                                        Copy Token
                                    </Button>
                                </Grid>
                            </>
                        )}

                        <Grid item xs={12}>
                            <Typography variant="h6" gutterBottom>
                                Refresh Your Token
                            </Typography>
                            <Typography variant="body1" paragraph>
                                Click below to refresh your token using the refresh token stored in session storage.
                            </Typography>
                            <Button variant="contained" color="primary" onClick={handleOpenConfirm}>
                                <Refresh /> Refresh Token
                            </Button>
                        </Grid>

                        <Grid item xs={12}>
                            <Typography variant="h6" gutterBottom>
                                Validate Your Token
                            </Typography>
                            <Button variant="contained" color="secondary" onClick={handleValidateToken}>
                                Validate Token
                            </Button>
                            {validationResult && (
                                <Typography variant="body1" paragraph sx={{ mt: 2 }}>
                                    {validationResult}
                                </Typography>
                            )}
                        </Grid>
                    </Grid>

                    {/* Confirmation Dialog for Refreshing Token */}
                    <Dialog open={openConfirm} onClose={handleCloseConfirm}>
                        <DialogTitle>Confirm Refresh</DialogTitle>
                        <DialogContent>
                            <DialogContentText>
                                Are you sure you want to refresh your token?
                            </DialogContentText>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={handleCloseConfirm} color="primary">
                                Cancel
                            </Button>
                            <Button onClick={handleRefreshToken} color="primary">
                                Confirm
                            </Button>
                        </DialogActions>
                    </Dialog>

                    {/* Success Notification */}
                    <Snackbar open={!!message} autoHideDuration={6000} onClose={() => setMessage('')}>
                        <Alert onClose={() => setMessage('')} severity="success" sx={{ width: '100%' }}>
                            {message}
                        </Alert>
                    </Snackbar>

                    {/* Error Notification */}
                    <Snackbar open={!!error} autoHideDuration={6000} onClose={() => setError('')}>
                        <Alert onClose={() => setError('')} severity="error" sx={{ width: '100%' }}>
                            {error}
                        </Alert>
                    </Snackbar>
                </Paper>
            </Container>
        </Box>
    );
};

export default ApiUsagePage;
