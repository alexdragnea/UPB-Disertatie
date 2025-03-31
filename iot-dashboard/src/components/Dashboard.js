import React, { useEffect, useState, useContext, useRef } from 'react';
import axios from 'axios';
import AuthContext from '../AuthContext';
import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography,
    CircularProgress, Box, Paper, Grid, Tooltip, IconButton, Divider, Chip,
    Alert, Badge, Fade
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import SensorsIcon from '@mui/icons-material/Sensors';
import SignalWifiStatusbar4BarIcon from '@mui/icons-material/SignalWifiStatusbar4Bar';
import SignalWifiConnectedNoInternet4Icon from '@mui/icons-material/SignalWifiConnectedNoInternet4';

const Dashboard = () => {
    const [sensors, setSensors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [lastUpdated, setLastUpdated] = useState(null);
    const [wsConnected, setWsConnected] = useState(false);
    const { user, loading: authLoading } = useContext(AuthContext);
    const userId = user?.userId;
    const wsRef = useRef(null);

    const fetchSensors = async (token) => {
        try {
            const response = await axios.get(`https://localhost:8888/v1/iot-core/measurements?userId=${userId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            return response.data.measurements || [];
        } catch (error) {
            console.error('Error fetching measurements:', error);
            throw error;
        }
    };

    const fetchInitialData = async () => {
        setLoading(true);
        setError(null);
        const token = sessionStorage.getItem('accessToken');

        try {
            const fetchedSensors = await fetchSensors(token);
            setSensors(fetchedSensors.map(sensor => ({ name: sensor, value: 'N/A', timestamp: null })));
            setLastUpdated(new Date().toLocaleTimeString());
        } catch {
            setError('Failed to fetch sensors.');
        } finally {
            setLoading(false);
        }
    };

    const setupWebSocket = () => {
        if (wsRef.current) wsRef.current.close();

        wsRef.current = new WebSocket('wss://localhost:8888/ws');

        wsRef.current.onopen = () => setWsConnected(true);
        wsRef.current.onclose = () => setWsConnected(false);
        wsRef.current.onerror = () => setWsConnected(false);

        wsRef.current.onmessage = (event) => {
            const data = JSON.parse(event.data);
            setSensors(prevSensors => prevSensors.map(sensor =>
                sensor.name === data.measurement
                    ? { ...sensor, value: data.value, timestamp: data.timestamp }
                    : sensor
            ));
        };
    };

    useEffect(() => {
        if (!authLoading && userId) {
            fetchInitialData();
            setupWebSocket();
        }

        return () => wsRef.current && wsRef.current.close();
    }, [userId, authLoading]);

    return (
        <Box sx={{fontFamily: 'Arial, sans-serif' }}>
            <Typography variant="h5" align="center" gutterBottom>
                Live Data
                <IconButton color="primary" onClick={fetchInitialData}>
                    <RefreshIcon />
                </IconButton>
            </Typography>

            <Box display="flex" justifyContent="center" alignItems="center" gap={2} mb={3}>
                <SensorsIcon sx={{ fontSize: 40, color: '#1976d2' }} />
                <Typography variant="caption" sx={{ color: 'gray' }}>
                    {lastUpdated ? `Last updated: ${lastUpdated}` : ''}
                </Typography>
                <Badge color={wsConnected ? "success" : "error"} variant="dot">
                    {wsConnected ? <SignalWifiStatusbar4BarIcon color="success" /> : <SignalWifiConnectedNoInternet4Icon color="error" />}
                </Badge>
                <Typography variant="caption" sx={{ color: wsConnected ? 'green' : 'red' }}>
                    {wsConnected ? 'Connected' : 'Disconnected'}
                </Typography>
            </Box>

            <Divider sx={{ mb: 3 }}>
                <Chip label="Feed" color="primary" />
            </Divider>

            {loading ? (
                <Box display="flex" justifyContent="center">
                    <CircularProgress />
                </Box>
            ) : error ? (
                <Alert severity="error" sx={{ textAlign: 'center' }}>{error}</Alert>
            ) : (
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <TableContainer component={Paper} sx={{ borderRadius: 3, boxShadow: 2, overflowX: 'auto' }}>
                            <Table stickyHeader>
                                <TableHead sx={{ backgroundColor: '#1976d2' }}>
                                    <TableRow>
                                        <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Measurement</TableCell>
                                        <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Value</TableCell>
                                        <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Timestamp</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {sensors.map(sensor => (
                                        <Fade in={true} key={sensor.name}>
                                            <TableRow sx={{ '&:nth-of-type(odd)': { backgroundColor: '#f9f9f9' }, '&:hover': { backgroundColor: '#e3f2fd', boxShadow: 2 } }}>
                                                <TableCell>
                                                    <Tooltip title={`View details for ${sensor.name}`} arrow>
                                                        <a href={`/sensor/${sensor.name}`} style={{ textDecoration: 'none', color: '#1976d2' }}>
                                                            {sensor.name}
                                                        </a>
                                                    </Tooltip>
                                                </TableCell>
                                                <TableCell>{sensor.value !== undefined ? sensor.value : 'N/A'}</TableCell>
                                                <TableCell>{sensor.timestamp ? new Date(sensor.timestamp).toLocaleString() : 'N/A'}</TableCell>
                                            </TableRow>
                                        </Fade>
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
