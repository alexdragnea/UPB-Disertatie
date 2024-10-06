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
} from '@mui/material';

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
        const token = localStorage.getItem('accessToken');

        try {
            const fetchedSensors = await fetchSensors(token);
            // Initialize sensors with "N/A" values
            const initializedSensors = fetchedSensors.map(sensor => ({
                name: sensor,  // Using the sensor name directly
                value: 'N/A', // Default value
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
        const measurement = data.message.measurement; // Access the measurement name
        const value = data.message.value; // Access the value
        const timestamp = data.timestamp; // Access the timestamp

        console.log('Live measurement received:', measurement, 'Value:', value, 'Timestamp:', timestamp);

        if (!measurement || value === undefined || !timestamp) {
            return; // Ignore invalid data
        }

        setSensors((prevSensors) => {
            return prevSensors.map(sensor => {
                if (sensor.name === measurement) {
                    // Update existing sensor value and timestamp
                    return { ...sensor, value, timestamp }; // Update value and timestamp
                }
                return sensor; // Return unchanged sensor
            });
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
        <Box sx={{ fontFamily: 'Arial, sans-serif', padding: 2 }}>
            <Typography variant="h5" component="h2" gutterBottom align="center">
                Live Data
            </Typography>

            {loading ? (
                <Box display="flex" justifyContent="center">
                    <CircularProgress />
                </Box>
            ) : error ? (
                <Typography color="error" align="center">{error}</Typography>
            ) : (
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <TableContainer component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell><strong>Measurement</strong></TableCell>
                                        <TableCell><strong>Value</strong></TableCell>
                                        <TableCell><strong>Timestamp</strong></TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {sensors.map((sensor) => (
                                        <TableRow key={sensor.name}>
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
                                            <TableCell>
                                                {sensor.value !== undefined ? sensor.value : 'N/A'}
                                            </TableCell>
                                            <TableCell>
                                                {sensor.timestamp ? new Date(sensor.timestamp).toLocaleString() : 'N/A'}
                                            </TableCell>
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
