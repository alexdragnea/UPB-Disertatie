import React, { useEffect, useState, useContext } from 'react';
import axios from 'axios';
import AuthContext from '../AuthContext';
import { FaSyncAlt } from 'react-icons/fa';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
    Select,
    MenuItem,
    CircularProgress,
    Box,
    Paper,
    IconButton,
} from '@mui/material';

const Dashboard = () => {
    const [sensors, setSensors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [measurements, setMeasurements] = useState([]);
    const [timeFilter, setTimeFilter] = useState('24h');
    const { user, loading: authLoading } = useContext(AuthContext);
    const userId = user?.userId;

    const getTimeRange = (filter) => {
        const endTime = new Date();
        let startTime;

        switch (filter) {
            case '24h':
                startTime = new Date(endTime.getTime() - 24 * 60 * 60 * 1000);
                break;
            case '12h':
                startTime = new Date(endTime.getTime() - 12 * 60 * 60 * 1000);
                break;
            case '1h':
                startTime = new Date(endTime.getTime() - 1 * 60 * 60 * 1000);
                break;
            case '30m':
                startTime = new Date(endTime.getTime() - 30 * 60 * 1000);
                break;
            case '5m':
                startTime = new Date(endTime.getTime() - 5 * 60 * 1000);
                break;
            default:
                startTime = new Date(endTime.getTime() - 24 * 60 * 60 * 1000);
        }

        return {
            startTime: startTime.toISOString(),
            endTime: endTime.toISOString(),
        };
    };

    const fetchUserMeasurements = async (token) => {
        try {
            const response = await axios.get(`http://localhost:8888/v1/iot-core/measurements?userId=${userId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            return response.data.measurements || [];
        } catch (error) {
            console.error('Error fetching user measurements:', error);
            throw error;
        }
    };

    const fetchMeasurementsData = async (token, measurementList) => {
        const { startTime, endTime } = getTimeRange(timeFilter);
        const results = []; // Array to hold results from all measurements
        let fetchErrors = []; // Array to log errors for measurements that fail

        const promises = measurementList.map(async (measurement) => {
            const filterData = {
                userId,
                measurement,
                startTime,
                endTime,
            };

            try {
                const response = await axios.post('http://localhost:8888/v1/iot-core/measurements-by-filter', filterData, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });
                const sensorData = processSensorData(response.data, measurement);
                results.push(sensorData); // Push successfully fetched data
            } catch (error) {
                console.error(`Error fetching data for ${measurement}:`, error); // Log the error
                fetchErrors.push(measurement); // Add to error list
            }
        });

        // Wait for all promises to settle (either fulfilled or rejected)
        await Promise.allSettled(promises);

        // Set the sensors state to the results fetched successfully
        setSensors(results);

        // Optionally, you could log the errors or notify the user
        if (fetchErrors.length > 0) {
            console.warn(`Failed to fetch data for the following measurements: ${fetchErrors.join(', ')}`);
        }
    };

    const processSensorData = (data, measurement) => {
        let lastValue = 'N/A';
        let maxValue = null;
        let minValue = null;
        let totalValue = 0;
        let count = 0;
        let unit = ''; // Variable to hold the unit

        if (data.length > 0) {
            data.sort((a, b) => new Date(b.time) - new Date(a.time));
            lastValue = data[0].value;
            unit = data[0].unit; // Extract the unit from the first data entry

            data.forEach(record => {
                const value = record.value;

                if (maxValue === null || value > maxValue) {
                    maxValue = value;
                }
                if (minValue === null || value < minValue) {
                    minValue = value;
                }

                totalValue += value;
                count++;
            });
        } else {
            lastValue = 'N/A';
            maxValue = 'N/A';
            minValue = 'N/A';
        }

        const avgValue = count > 0 ? (totalValue / count).toFixed(2) : 'N/A';

        return {
            name: measurement,
            lastValue: `${lastValue} ${unit}`, // Include the unit
            maxValue: maxValue !== null ? `${maxValue} ${unit}` : 'N/A', // Include the unit
            minValue: minValue !== null ? `${minValue} ${unit}` : 'N/A', // Include the unit
            avgValue: `${avgValue} ${unit}`, // Include the unit
        };
    };

    const fetchData = async () => {
        setLoading(true);
        setError(null);
        const token = localStorage.getItem('accessToken');

        try {
            const fetchedMeasurements = await fetchUserMeasurements(token);
            setMeasurements(fetchedMeasurements);
            if (fetchedMeasurements.length > 0) {
                await fetchMeasurementsData(token, fetchedMeasurements);
            }
        } catch (err) {
            setError('Failed to fetch measurements');
        } finally {
            setLoading(false);
        }
    };

    const handleRefreshData = () => {
        fetchData(); // Fetch data with the current filter
    };

    useEffect(() => {
        if (!authLoading && userId) {
            fetchData();
        }
    }, [userId, authLoading]);

    const handleTimeFilterChange = (e) => {
        setTimeFilter(e.target.value);
        // Do not fetch data here
    };

    return (
        <Box sx={{ fontFamily: 'Arial, sans-serif' }}>
            <Typography variant="h8" component="h4" gutterBottom>Measurement summary</Typography>

            {/* Flexbox Container for Filter and Refresh Button */}
            <Box sx={{ display: 'flex', alignItems: 'center', marginBottom: 2 }}>
                <Select
                    id="timeFilter"
                    value={timeFilter}
                    onChange={handleTimeFilterChange}
                    sx={{ minWidth: 150, marginRight: 2 }} // Add margin to separate from button
                >
                    <MenuItem value="24h">Last 24 Hours</MenuItem>
                    <MenuItem value="12h">Last 12 Hours</MenuItem>
                    <MenuItem value="1h">Last Hour</MenuItem>
                    <MenuItem value="30m">Last 30 Minutes</MenuItem>
                    <MenuItem value="5m">Last 5 Minutes</MenuItem>
                </Select>

                <IconButton 
                    variant="contained"
                    color="primary"
                    onClick={handleRefreshData}
                    sx={{ backgroundColor: '#1976d2', color: '#fff', '&:hover': { backgroundColor: '#0056b3' } }}
                >
                    <FaSyncAlt />
                </IconButton>
            </Box>

            {loading ? (
                <CircularProgress />
            ) : error ? (
                <Typography color="error">{error}</Typography>
            ) : (
                <>
                    <Typography variant="h5" component="h2" gutterBottom>Measurements</Typography>
                    {sensors.length > 0 ? (
                        <TableContainer component={Paper} sx={{ marginTop: 2 }}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Sensor Name</TableCell>
                                        <TableCell>Last Value</TableCell>
                                        <TableCell>Max Value</TableCell>
                                        <TableCell>Min Value</TableCell>
                                        <TableCell>Avg Value</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {sensors.map(sensor => (
                                        <TableRow key={sensor.name}>
                                            <TableCell>
                                                <a href={`/sensor/${sensor.name}`} style={{ textDecoration: 'none', color: '#1976d2' }}>
                                                    {sensor.name}
                                                </a>
                                            </TableCell>
                                            <TableCell>{sensor.lastValue}</TableCell>
                                            <TableCell>{sensor.maxValue}</TableCell>
                                            <TableCell>{sensor.minValue}</TableCell>
                                            <TableCell>{sensor.avgValue}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    ) : (
                        <Typography>No measurements available.</Typography>
                    )}
                </>
            )}
        </Box>
    );
};

export default Dashboard;
