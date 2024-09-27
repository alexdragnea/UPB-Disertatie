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
    CircularProgress,
} from '@mui/material';
import { CopyAll, Refresh, DeviceHub } from '@mui/icons-material';

const ApiUsagePage = () => {
    const [apiKey, setApiKey] = useState(''); // Store API key here
    const [userId, setUserId] = useState('');
    const [showApiKey, setShowApiKey] = useState(false); // State to control visibility of API key
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [tabValue, setTabValue] = useState(0);
    const [validationResult, setValidationResult] = useState('');
    const [openConfirm, setOpenConfirm] = useState(false);
    const [commandExample, setCommandExample] = useState('');

    useEffect(() => {
        fetchUserIdAndApiKey();
    }, []);

    useEffect(() => {
        if (userId && apiKey) {
            setCommandExample(renderCommandExample(userId, apiKey));
        }
    }, [tabValue, userId, apiKey]);

    const fetchUserIdAndApiKey = async () => {
        setLoading(true);
        const token = localStorage.getItem('accessToken');
        if (token) {
            try {
                const userResponse = await fetch('http://localhost:8888/v1/iot-user/logged', {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (!userResponse.ok) throw new Error('Failed to fetch user details');
                const userData = await userResponse.json();
                setUserId(userData.userId);

                const apiKeyResponse = await fetch('http://localhost:8888/v1/iot-user/api-key', {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (!apiKeyResponse.ok) throw new Error('Failed to fetch API key');
                const apiKeyData = await apiKeyResponse.json();
                setApiKey(apiKeyData.apiKey); // Store API key in state
                setMessage('API key fetched successfully!');

                setCommandExample(renderCommandExample(userData.userId, apiKeyData.apiKey));
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        } else {
            setError('No Bearer token found. Please log in first.');
            setLoading(false);
        }
    };

    const handleCopyApiKey = () => {
        navigator.clipboard.writeText(apiKey);
        setMessage('API key copied to clipboard!');
    };

    const handleOpenConfirm = () => setOpenConfirm(true);
    const handleCloseConfirm = () => setOpenConfirm(false);
    const handleRefreshApiKey = async () => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8888/v1/iot-user/refresh-api-key', {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
                },
            });
    
            if (response.ok) {
                const text = await response.text(); // Get the plain text response
                if (text === "API key refreshed successfully") {
                    // If the response is the expected success message
                    // Fetch the new API key to update the state
                    const apiKeyResponse = await fetch('http://localhost:8888/v1/iot-user/api-key', {
                        method: 'GET',
                        headers: {
                            Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
                        },
                    });
                    if (!apiKeyResponse.ok) {
                        throw new Error('Failed to fetch new API key after refresh');
                    }
                    const apiKeyData = await apiKeyResponse.json();
                    setApiKey(apiKeyData.apiKey); // Update state with new API key
                    setCommandExample(renderCommandExample(userId, apiKeyData.apiKey)); // Update command example
                    setMessage('API key refreshed successfully!');
                } else {
                    setError(`Failed to refresh API key: ${text}`); // Handle unexpected message
                }
            } else {
                const errorText = await response.text(); // Get error response
                setError(`Failed to refresh API key: ${errorText}`); // Set error message
            }
        } catch (err) {
            setError('An error occurred while refreshing the API key: ' + err.message);
        } finally {
            setLoading(false);
            handleCloseConfirm();
        }
    };
    
    const handleValidateApiKey = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8888/v1/iot-user/validate-api-key?userId=${userId}`, {
                method: 'GET',
                headers: {
                    'x-api-key': apiKey,
                },
            });
            if (!response.ok) {
                const err = await response.json();
                setError('API key validation failed: ' + err.message);
                return;
            }
            setValidationResult('API key validated successfully!');
            setMessage('Your API key is valid!');
        } catch (err) {
            setError('An error occurred during API key validation.');
        } finally {
            setLoading(false);
        }
    };

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
    };

    const renderCommandExample = (userIdString, apiKey) => {
        switch (tabValue) {
            case 0:
                return (
                    `curl --location 'http://localhost:8888/v1/iot-bridge' \\
--header 'x-api-key: ${apiKey}' \\
--header 'Content-Type: application/json' \\
--data '{
    "measurement": "temperature",
    "sensorName": "living_room_sensor",
    "userId": "${userIdString}",
    "value": 22.5,
    "unit": "C"
}'`
                );
            case 1:
                return (
                    `POST http://localhost:8888/v1/iot-bridge
Headers:
x-api-key: ${apiKey}
Content-Type: application/json

Body:
{
    "measurement": "humidity",
    "sensorName": "bathroom_sensor",
    "userId": "${userIdString}",
    "value": 60,
    "unit": "%"
}`
                );
            case 2:
                return (
                    `fetch('http://localhost:8888/v1/iot-bridge', {
    method: 'POST',
    headers: {
        'x-api-key': '${apiKey}',
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
.then(data => console.log(data));`
                );
            default:
                return '';
        }
    };

    const handleCopyCommand = () => {
        navigator.clipboard.writeText(commandExample);
        setMessage('Command copied to clipboard!');
    };

    const handleShowApiKey = () => {
        setShowApiKey((prevShowApiKey) => !prevShowApiKey); // Toggle the visibility
    };

    return (
        <Box
        sx={{
            display: 'flex',
            flexDirection: 'column',
            minHeight: '100vh',
            backgroundColor: '#f9f9f9',
            paddingY: 3,
            width: '100%',
        }}
    >
        <CssBaseline />

        <Container sx={{ mt: 4, mb: 4, width: '100%', maxWidth: 'lg', paddingX: { xs: 2, sm: 4 } }}>
                <Paper sx={{ padding: { xs: 2, sm: 3 }, borderRadius: 2, boxShadow: 3, backgroundColor: '#fff' }}>
                    <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1976d2' }}>
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
                        <pre style={{ backgroundColor: '#f8f8f8', padding: 10, borderRadius: 5 }}>
                            {commandExample}
                        </pre>
                    </Box>

                    <Grid container spacing={2} sx={{ marginTop: 3 }}>
                        <Grid item xs={12}>
                            <Typography variant="h6" gutterBottom>
                                Your API Key
                            </Typography>
                            <TextField
                                value={showApiKey ? apiKey : '●●●●●●●●●●●●●●●●●●●●'} // Mask API key
                                variant="outlined"
                                fullWidth
                                InputProps={{
                                    readOnly: true,
                                }}
                                type={showApiKey ? 'text' : 'password'} // Change type based on showApiKey state
                            />
                            <Button
                                onClick={handleShowApiKey} // Call to toggle the API key visibility
                                variant="outlined"
                                sx={{ mt: 1 }}
                            >
                                {showApiKey ? 'Hide API Key' : 'Show API Key'}
                            </Button>
                            <Button
                                onClick={handleCopyApiKey}
                                variant="contained"
                                sx={{ mt: 1, ml: 1 }}
                            >
                                Copy API Key
                            </Button>
                        </Grid>

                        <Grid item xs={12}>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleValidateApiKey}
                                sx={{ mr: 2 }}
                                disabled={loading}
                            >
                                Validate API Key
                                {loading && <CircularProgress size={24} sx={{ marginLeft: 1 }} />}
                            </Button>
                            <Button
                                variant="outlined"
                                color="secondary"
                                onClick={handleOpenConfirm}
                                disabled={loading}
                            >
                                Refresh API Key
                            </Button>
                        </Grid>
                    </Grid>

                    {/* Snackbar for messages */}
                    <Snackbar open={!!message} autoHideDuration={6000} onClose={() => setMessage('')}>
                        <Alert onClose={() => setMessage('')} severity="success">
                            {message}
                        </Alert>
                    </Snackbar>

                    {/* Snackbar for errors */}
                    <Snackbar open={!!error} autoHideDuration={6000} onClose={() => setError('')}>
                        <Alert onClose={() => setError('')} severity="error">
                            {error}
                        </Alert>
                    </Snackbar>

                    {/* Confirm Dialog for refreshing API Key */}
                    <Dialog open={openConfirm} onClose={handleCloseConfirm}>
                        <DialogTitle>Refresh API Key</DialogTitle>
                        <DialogContent>
                            <DialogContentText>
                                Are you sure you want to refresh your API key? This action cannot be undone.
                            </DialogContentText>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={handleCloseConfirm} color="primary">
                                Cancel
                            </Button>
                            <Button onClick={handleRefreshApiKey} color="primary" autoFocus>
                                Refresh
                            </Button>
                        </DialogActions>
                    </Dialog>
                </Paper>
            </Container>
        </Box>
    );
};

export default ApiUsagePage;
