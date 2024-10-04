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
    const [tableData, setTableData] = useState([]);
    const [minValue, setMinValue] = useState(null);
    const [maxValue, setMaxValue] = useState(null);
    const [avgValue, setAvgValue] = useState(null);
    const [error, setError] = useState('');
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [chartType, setChartType] = useState('line');
    const [filterOption, setFilterOption] = useState('last7days');
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [lastMeasurementTime, setLastMeasurementTime] = useState(null);
    const [isOnline, setIsOnline] = useState(false);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);

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

    const fetchData = async () => {
        setLoading(true);
        setError('');
        try {
            let token = localStorage.getItem('accessToken');
            if (!token) {
                token = await refreshToken();
            }

            const calculatedStartTime = startTime || getStartTime(filterOption);
            const calculatedEndTime = endTime || new Date().toISOString();

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
            const labels = measurements.map(m => format(new Date(m.time), 'MMM d, yyyy, h:mm a'));
            const dataValues = measurements.map(m => m.value);
            const unit = measurements.length > 0 ? measurements[0].unit : '';
            const lastTime = measurements.length > 0 ? new Date(measurements[measurements.length - 1].time) : null;

            // Calculate Min, Max, and Average
            const minVal = Math.min(...dataValues);
            const maxVal = Math.max(...dataValues);
            const avgVal = dataValues.reduce((sum, val) => sum + val, 0) / dataValues.length;

            setMinValue(minVal);
            setMaxValue(maxVal);
            setAvgValue(avgVal.toFixed(2));

            setGraphData({
                labels,
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

            setTableData(measurements); // Store the measurements for the table

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
    }, [sensorId, startTime, endTime, filterOption]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (filterOption === 'custom' && (!startTime || !endTime)) {
            setError('Please select both start and end times for the custom date range.');
            setSnackbarOpen(true);
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
            setStartTime('');
            setEndTime('');
        }
    };

    // Handle pagination
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0); // Reset to first page
    };

    // Function to handle snackbar close
    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    if (loading) {
        return <div style={{ textAlign: 'center' }}><CircularProgress /></div>;
    }

    return (
        <Paper style={{ padding: 20, height: 'auto', display: 'flex', flexDirection: 'column' }}>
            <Typography variant="h6" gutterBottom style={{ textAlign: 'center' }}>
                {sensorId}
            </Typography>

            {/* Sensor Status */}
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

            {/* Filter Form */}
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

            {/* Chart Type Selector */}
            <Grid container spacing={1} alignItems="center" justifyContent="center" style={{ marginBottom: 10 }}>
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

            {/* Conditionally Render the Chart */}
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
                                        text: `Measurement Data for ${sensorId} (${graphData.datasets[0].label})`,
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
                                        text: `Measurement Data for ${sensorId} (${graphData.datasets[0].label})`,
                                    },
                                },
                            }}
                        />
                    </div>
                )}
            </div>

            {/* Summary Section */}
            <Grid container spacing={2} justifyContent="center" style={{ marginTop: 20 }}>
                <Grid item>
                    <Typography variant="body1">Min: {minValue}</Typography>
                </Grid>
                <Grid item>
                    <Typography variant="body1">Max: {maxValue}</Typography>
                </Grid>
                <Grid item>
                    <Typography variant="body1">Average: {avgValue}</Typography>
                </Grid>
            </Grid>

            {/* Data Table with Pagination */}
            <TableContainer component={Paper} style={{ marginTop: 20 }}>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>Date & Time</TableCell>
                            <TableCell align="right">Value</TableCell>
                            <TableCell align="right">Unit</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {tableData.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row) => (
                            <TableRow key={row.time}>
                                <TableCell>{format(new Date(row.time), 'MMM d, yyyy, h:mm a')}</TableCell>
                                <TableCell align="right">{row.value}</TableCell>
                                <TableCell align="right">{row.unit}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                component="div"
                count={tableData.length}
                page={page}
                onPageChange={handleChangePage}
                rowsPerPage={rowsPerPage}
                onRowsPerPageChange={handleChangeRowsPerPage}
                rowsPerPageOptions={[5, 10, 25]}
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
