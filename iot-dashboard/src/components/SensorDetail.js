import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { format } from 'date-fns';

// MUI imports
import {
    Paper,
    CircularProgress,
    Typography,
    TextField,
    Button,
    Grid,
    MenuItem,
    Snackbar,
    Alert,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TablePagination,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle'; // Green check mark for online
import CancelIcon from '@mui/icons-material/Cancel'; // Red cross for offline

// Chart.js components
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    BarElement,
    ArcElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';
import { Line, Bar, Pie } from 'react-chartjs-2';

// Import AuthContext
import AuthContext from '../AuthContext';

// Register the necessary components
ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, BarElement, ArcElement, Title, Tooltip, Legend);

export default function SensorDetail() {
    const { id: sensorId } = useParams();
    const { user, refreshToken } = useContext(AuthContext);
    const [loading, setLoading] = useState(true);
    const [graphData, setGraphData] = useState(null);
    const [tableData, setTableData] = useState([]); // State for table data
    const [error, setError] = useState('');
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [chartType, setChartType] = useState('line');
    const [filterOption, setFilterOption] = useState('last7days');
    const [aggregationLevel, setAggregationLevel] = useState('minute');
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [lastMeasurementTime, setLastMeasurementTime] = useState(null);
    const [isOnline, setIsOnline] = useState(false);

    // Pagination state
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5); // Adjust number of rows per page as needed

    // Function to calculate start time based on filter option
    const getStartTime = (option) => {
        const now = new Date();
        switch (option) {
            case 'last7days':
                return new Date(now.setDate(now.getDate() - 7)).toISOString();
            case 'last30days':
                return new Date(now.setDate(now.getDate() - 30)).toISOString();
            case 'last90days':
                return new Date(now.setDate(now.getDate() - 90)).toISOString();
            default:
                return ''; // Default to empty for custom range
        }
    };

    // Function to aggregate data based on selected interval
    const aggregateData = (measurements, interval) => {
        const aggregated = {};

        measurements.forEach((measurement) => {
            const date = new Date(measurement.time);
            let key;

            switch (interval) {
                case 'minute':
                    key = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`;
                    break;
                case 'hour':
                    key = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}`;
                    break;
                case 'day':
                    key = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
                    break;
                default:
                    return;
            }

            if (!aggregated[key]) {
                aggregated[key] = { sum: 0, count: 0 };
            }
            aggregated[key].sum += measurement.value;
            aggregated[key].count += 1;
        });

        return Object.entries(aggregated).map(([key, { sum, count }]) => ({
            time: key,
            value: sum / count, // Calculate average
        }));
    };

    const fetchData = async () => {
        setLoading(true);
        setError('');
        try {
            let token = localStorage.getItem('accessToken');
            if (!token) {
                token = await refreshToken();
            }

            // Determine the start and end time based on the selected filter option
            const calculatedStartTime = startTime || getStartTime(filterOption);
            const calculatedEndTime = endTime || new Date().toISOString();

            // Ensure that if "custom" is selected, both times are set
            if (filterOption === 'custom' && (!startTime || !endTime)) {
                throw new Error('Please select both start and end times for the custom date range.');
            }

            const response = await axios.post('http://localhost:8888/v1/iot-core/measurements-by-filter', {
                userId: user.userId,
                measurement: sensorId,
                startTime: calculatedStartTime,
                endTime: calculatedEndTime
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            const measurements = response.data;
            const unit = measurements.length > 0 ? measurements[0].unit : '';
            const lastTime = measurements.length > 0 ? new Date(measurements[measurements.length - 1].time) : null;

            // Aggregate data based on selected aggregation level
            const aggregatedMeasurements = aggregateData(measurements, aggregationLevel);

            // Prepare data for charts
            const labels = aggregatedMeasurements.map(m => m.time);
            const dataValues = aggregatedMeasurements.map(m => m.value);

            setGraphData({
                labels: labels,
                datasets: [
                    {
                        label: `${sensorId} (${unit})`,
                        data: dataValues,
                        borderColor: 'rgba(0, 123, 255, 1)', // Improved color
                        backgroundColor: 'rgba(0, 123, 255, 0.2)', // Improved color
                        fill: true,
                    },
                ],
            });

            setTableData(measurements); // Save raw measurements for the table display

            // Update sensor online status based on the last measurement time
            if (lastTime) {
                const isSensorOnline = (new Date() - lastTime) <= 5 * 60 * 1000; // 5 minutes
                setIsOnline(isSensorOnline);
                setLastMeasurementTime(lastTime);
            }
        } catch (error) {
            console.error('Error fetching data:', error);
            setError(error.message || 'Failed to fetch measurement data');
            setSnackbarOpen(true); // Open snackbar to show error
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (user) {
            const initialStartTime = getStartTime('last7days');
            const initialEndTime = new Date().toISOString();
            setStartTime(initialStartTime);
            setEndTime(initialEndTime);
            setFilterOption('last7days'); // Default filter option
            fetchData();
        }
    }, [user]);

    useEffect(() => {
        fetchData(); // Fetch data when time or filter option changes
    }, [sensorId, startTime, endTime, filterOption, aggregationLevel]);

    const handleSubmit = (e) => {
        e.preventDefault();
        // Validate for custom range
        if (filterOption === 'custom' && (!startTime || !endTime)) {
            setError('Please select both start and end times for the custom date range.');
            setSnackbarOpen(true); // Open snackbar to show error
            return;
        }
        fetchData(); // Fetch data based on the selected filter
    };

    const handleFilterChange = (event) => {
        const option = event.target.value;
        setFilterOption(option);

        if (option !== 'custom') {
            const newStartTime = getStartTime(option);
            const newEndTime = new Date().toISOString();
            setStartTime(newStartTime);
            setEndTime(newEndTime);
        } else {
            // Reset to empty for custom date range
            setStartTime('');
            setEndTime('');
        }
    };

    const handleAggregationChange = (event) => {
        setAggregationLevel(event.target.value); // Set aggregation level based on user selection
    };

    // Function to handle snackbar close
    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    // Handle pagination changes
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    if (loading) {
        return <div style={{ textAlign: 'center' }}><CircularProgress /></div>;
    }

    return (
        <Paper
            style={{
                padding: 20,
                height: 'auto',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'flex-start',
                boxSizing: 'border-box',
                overflow: 'hidden',
            }}
        >
            <Typography variant="h6" gutterBottom style={{ textAlign: 'center' }}>
                {sensorId}
            </Typography>

            {/* Sensor Status with Icons and Message */}
            <Grid container alignItems="center" justifyContent="center">
                {isOnline ? (
                    <CheckCircleIcon color="success" fontSize="large" />
                ) : (
                    <CancelIcon color="error" fontSize="large" />
                )}
                <Typography variant="body2" style={{ marginLeft: 8 }}>
                    {isOnline ? 'Sensor is online' : 'Sensor is offline'}
                </Typography>
            </Grid>
            {lastMeasurementTime && (
                <Typography variant="body2" style={{ textAlign: 'center', marginTop: 5 }}>
                    Last data received: {format(lastMeasurementTime, 'MMM d, yyyy, h:mm a')}
                </Typography>
            )}

            {/* Filter Inputs with Smaller and Minimalistic Design */}
            <form onSubmit={handleSubmit} style={{ marginBottom: 10, marginTop: 10 }}>
                <Grid container spacing={1} alignItems="center" justifyContent="center">
                    <Grid item xs={6} sm={4}>
                        <TextField
                            label="Filter"
                            select
                            value={filterOption}
                            onChange={handleFilterChange}
                            fullWidth
                            size="small"
                            variant="outlined"
                        >
                            <MenuItem value="last7days">Last 7 Days</MenuItem>
                            <MenuItem value="last30days">Last 30 Days</MenuItem>
                            <MenuItem value="last90days">Last 90 Days</MenuItem>
                            <MenuItem value="custom">Custom</MenuItem>
                        </TextField>
                    </Grid>
                    {filterOption === 'custom' && (
                        <>
                            <Grid item xs={6} sm={3}>
                                <TextField
                                    label="Start"
                                    type="datetime-local"
                                    value={startTime ? startTime.substring(0, 16) : ''}
                                    onChange={(e) => {
                                        const date = new Date(e.target.value);
                                        setStartTime(date.toISOString());
                                    }}
                                    fullWidth
                                    InputLabelProps={{ shrink: true }}
                                    size="small"
                                    variant="outlined"
                                />
                            </Grid>
                            <Grid item xs={6} sm={3}>
                                <TextField
                                    label="End"
                                    type="datetime-local"
                                    value={endTime ? endTime.substring(0, 16) : ''}
                                    onChange={(e) => {
                                        const date = new Date(e.target.value);
                                        setEndTime(date.toISOString());
                                    }}
                                    fullWidth
                                    InputLabelProps={{ shrink: true }}
                                    size="small"
                                    variant="outlined"
                                />
                            </Grid>
                        </>
                    )}
                    <Grid item xs={6} sm={2}>
                        <Button type="submit" variant="contained" color="primary" fullWidth size="small">
                            Filter
                        </Button>
                    </Grid>
                </Grid>
            </form>

            {/* Aggregation Level Selector */}
            <Grid container spacing={1} alignItems="center" justifyContent="center" style={{ marginBottom: 10 }}>
                <Grid item xs={6}>
                    <TextField
                        label="Aggregation Level"
                        select
                        value={aggregationLevel}
                        onChange={handleAggregationChange}
                        fullWidth
                        size="small"
                        variant="outlined"
                    >
                        <MenuItem value="minute">Minute</MenuItem>
                        <MenuItem value="hour">Hour</MenuItem>
                        <MenuItem value="day">Day</MenuItem>
                    </TextField>
                </Grid>
            </Grid>

            {/* Chart Type Selector */}
            <Grid container spacing={1} alignItems="center" justifyContent="center" style={{ marginBottom: 0 }}>
                <Grid item xs={4}>
                    <TextField
                        label="Chart Type"
                        select
                        value={chartType}
                        onChange={(e) => setChartType(e.target.value)}
                        fullWidth
                        SelectProps={{
                            native: true,
                        }}
                        size="small"
                    >
                        <option value="line">Line</option>
                        <option value="bar">Bar</option>
                        <option value="pie">Pie</option>
                    </TextField>
                </Grid>
            </Grid>

            {/* Conditionally Render the Chart with Consistent Dimensions */}
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginTop: 0 }}>
                {chartType === 'line' && graphData && (
                    <div style={{ width: '100%', height: '520px' }}>
                        <Line
                            data={graphData}
                            options={{
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: { position: 'top' },
                                    tooltip: {
                                        callbacks: {
                                            label: (tooltipItem) => {
                                                return `${tooltipItem.dataset.label}: ${tooltipItem.raw.toFixed(2)}`; // Show values with 2 decimals
                                            }
                                        }
                                    }
                                },
                                scales: {
                                    x: {
                                        title: {
                                            display: true,
                                            text: 'Time',
                                        },
                                    },
                                    y: {
                                        title: {
                                            display: true,
                                            text: `${graphData.datasets[0].label}`,
                                        },
                                        beginAtZero: true,
                                    },
                                },
                            }}
                        />
                    </div>
                )}
                {chartType === 'bar' && graphData && (
                    <div style={{ width: '100%', height: '520px' }}>
                        <Bar
                            data={graphData}
                            options={{
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: { position: 'top' },
                                    title: {
                                        display: true,
                                    },
                                    tooltip: {
                                        callbacks: {
                                            label: (tooltipItem) => {
                                                return `${tooltipItem.dataset.label}: ${tooltipItem.raw.toFixed(2)}`; // Show values with 2 decimals
                                            }
                                        }
                                    }
                                },
                                scales: {
                                    x: {
                                        title: {
                                            display: true,
                                            text: 'Time',
                                        },
                                    },
                                    y: {
                                        title: {
                                            display: true,
                                            text: `${graphData.datasets[0].label}`,
                                        },
                                        beginAtZero: true,
                                    },
                                },
                            }}
                        />
                    </div>
                )}
                {chartType === 'pie' && graphData && (
                    <div style={{ width: '100%', height: '520px' }}>
                        <Pie
                            data={graphData}
                            options={{
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: { position: 'top' },
                                    title: {
                                        display: true,
                                    },
                                },
                            }}
                        />
                    </div>
                )}
            </div>

            {/* Table for Measurement Data */}
            <Typography variant="h6" style={{ marginTop: 20 }}>
                Table
            </Typography>
            <TableContainer>
                <Table style={{ minWidth: 300 }}> {/* Adjusted minWidth for a smaller table */}
                    <TableHead>
                        <TableRow>
                            <TableCell style={{ fontSize: '0.9rem' }}>Time</TableCell>
                            <TableCell style={{ fontSize: '0.9rem' }}>Value</TableCell>
                            <TableCell style={{ fontSize: '0.9rem' }}>Unit</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {tableData.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((measurement, index) => (
                            <TableRow key={index}>
                                <TableCell style={{ fontSize: '0.8rem' }}>{format(new Date(measurement.time), 'MMM d, yyyy, h:mm a')}</TableCell>
                                <TableCell style={{ fontSize: '0.8rem' }}>{measurement.value.toFixed(2)}</TableCell>
                                <TableCell style={{ fontSize: '0.8rem' }}>{measurement.unit}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                rowsPerPageOptions={[5, 10, 25]}
                component="div"
                count={tableData.length}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />

            {/* Snackbar for Error Messages */}
            <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity="error" sx={{ width: '100%' }}>
                    {error}
                </Alert>
            </Snackbar>
        </Paper> 
    );
}
