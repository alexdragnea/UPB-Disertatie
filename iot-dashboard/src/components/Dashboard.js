import React, { useEffect, useState, useContext, useRef } from 'react';
import axios from 'axios';
import AuthContext from '../AuthContext';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
    CircularProgress,
    Box,
    Paper,
    Grid,
    Tooltip,
    IconButton,
    Divider,
    Chip,
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import SensorsIcon from '@mui/icons-material/Sensors';

const Dashboard = () => {
    const [sensors, setSensors] = useState([]);  // List of sensor objects
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { user, loading: authLoading } = useContext(AuthContext);
    const userId = user?.userId;

    const wsRef = useRef(null);  // WebSocket reference to manage connection

    // Fetch all sensors and their measurements
    const fetchSensors = async (token) => {
        try {
            const response = await axios.get(`https://localhost:8888/v1/iot-core/measurements?userId=${userId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            return response.data.measurements || [];
        } catch (error) {
            console.error('Error fetching user measurements:', error);
            throw error;
        }
    };

    // Fetch initial data and update sensor list
    const fetchInitialData = async () => {
        setLoading(true);
        setError(null);
        const token = sessionStorage.getItem('accessToken');

        try {
            const fetchedSensors = await fetchSensors(token);
            const uniqueSensors = [...new Set(fetchedSensors)]; // Ensure unique names

            const initializedSensors = uniqueSensors.map(sensor => ({
                name: sensor,
                value: 'N/A',
                timestamp: null,
            }));
            setSensors(initializedSensors);  // Set the fetched sensors
        } catch (err) {
            setError('Failed to fetch sensors.');
        } finally {
            setLoading(false);
        }
    };

    // WebSocket setup to handle live data updates
    const setupWebSocket = () => {
        if (wsRef.current) {
            wsRef.current.close();  // Close the previous WebSocket connection if it exists
        }

        wsRef.current = new WebSocket('wss://localhost:8888/ws');

        wsRef.current.onopen = () => {
            console.log('WebSocket connection opened.');
        };

        wsRef.current.onmessage = (event) => {
            const data = JSON.parse(event.data);
            console.log('Received live data:', data);
            processLiveData(data);
        };

        wsRef.current.onerror = (error) => {
            console.error('WebSocket error:', error);
        };

        wsRef.current.onclose = () => {
            console.warn('WebSocket connection closed.');
        };
    };

    // Process incoming WebSocket live data
    const processLiveData = (data) => {
        console.log("Live measurement received:", data.measurement, "Value:", data.value, "Timestamp:", data.timestamp);

        if (!data.measurement || data.value === undefined || !data.timestamp) {
            console.warn("Received incomplete data:", data);
            return;
        }

        setSensors((prevSensors) => {
            console.log("Previous Sensors:", prevSensors);

            const updatedSensors = prevSensors.map((sensor) =>
                sensor.name === data.measurement
                    ? { ...sensor, value: data.value, timestamp: data.timestamp }
                    : sensor
            );

            console.log("Updated Sensors:", updatedSensors);
            return updatedSensors;
        });
    };


    useEffect(() => {
        if (!authLoading && userId) {
            fetchInitialData();
            setupWebSocket();
        }

        return () => {
            if (wsRef.current) {
                wsRef.current.close();  // Clean up WebSocket when component unmounts
            }
        };
    }, [userId, authLoading]);

    return (
        <Box sx={{ fontFamily: 'Arial, sans-serif', padding: 4 }}>
            <Typography variant="h4" component="h2" gutterBottom align="center" sx={{ marginBottom: 4 }}>
                Live Data
            </Typography>

            <Box display="flex" justifyContent="center" alignItems="center" mb={3}>
                <SensorsIcon sx={{ fontSize: 40, color: '#1976d2' }} />
                <Typography variant="h6" sx={{ marginLeft: 2 }}>
                    Sensor Measurements
                </Typography>
                <IconButton
                    color="primary"
                    onClick={fetchInitialData}
                    sx={{ marginLeft: 2 }}
                >
                    <RefreshIcon />
                </IconButton>
            </Box>

            <Divider sx={{ marginBottom: 3 }}>
                <Chip label="Live Data Feed" color="primary" />
            </Divider>

            {loading ? (
                <Box display="flex" justifyContent="center">
                    <CircularProgress />
                </Box>
            ) : error ? (
                <Typography color="error" align="center">{error}</Typography>
            ) : (
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <TableContainer component={Paper} elevation={3} sx={{ borderRadius: 3 }}>
                            <Table sx={{ minWidth: 650 }} aria-label="sensor data table">
                                <TableHead sx={{ backgroundColor: '#1976d2' }}>
                                    <TableRow>
                                        <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Measurement</TableCell>
                                        <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Value</TableCell>
                                        <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Timestamp</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {sensors.map((sensor) => (
                                        <TableRow
                                            key={sensor.name}
                                            sx={{
                                                '&:nth-of-type(odd)': { backgroundColor: '#f9f9f9' },
                                                '&:hover': { backgroundColor: '#e3f2fd' },
                                            }}
                                        >
                                            <TableCell>
                                                <Tooltip title={`View details for ${sensor.name}`} arrow>
                                                    <a
                                                        href={`/sensor/${sensor.name}`}
                                                        style={{ textDecoration: 'none', color: '#1976d2' }}
                                                    >
                                                        {sensor.name}
                                                    </a>
                                                </Tooltip>
                                            </TableCell>
                                            <TableCell>{sensor.value !== undefined ? sensor.value : 'N/A'}</TableCell>
                                            <TableCell>{sensor.timestamp ? new Date(sensor.timestamp).toLocaleString() : 'N/A'}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Grid>
                </Grid>
            )}
        </Box>
    );

};

export default Dashboard;
