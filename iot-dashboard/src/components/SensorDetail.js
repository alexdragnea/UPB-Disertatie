import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { format } from 'date-fns';
import {
    Chart as ChartJS,
    LineElement,
    BarElement,
    ArcElement,
    PointElement,
    LineController,
    BarController,
    PieController,
    RadarController,
    CategoryScale,
    LinearScale,
    Title,
    Tooltip,
    Legend,
    Filler,
    TimeScale,
    RadialLinearScale
} from 'chart.js';
import zoomPlugin from 'chartjs-plugin-zoom';
import {
    Paper,
    CircularProgress,
    Typography,
    IconButton,
    Grid,
    TextField,
    MenuItem,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TablePagination,
    Snackbar,
    Alert,
    InputAdornment,
    Popover,
    Tooltip as MuiTooltip,
    Box
} from '@mui/material';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import FilterListIcon from '@mui/icons-material/FilterList';
import TableChartIcon from '@mui/icons-material/TableChart';
import ShowChartIcon from '@mui/icons-material/ShowChart';
import TimelineIcon from '@mui/icons-material/Timeline';
import BarChartIcon from '@mui/icons-material/BarChart';
import PieChartIcon from '@mui/icons-material/PieChart';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import RadarIcon from '@mui/icons-material/Radar';
import SignalCellularAltIcon from '@mui/icons-material/SignalCellularAlt';
import SearchIcon from '@mui/icons-material/Search';
import { Line, Bar, Pie, Radar } from 'react-chartjs-2';
import AuthContext from '../AuthContext';

ChartJS.register(
    LineElement,
    BarElement,
    ArcElement,
    PointElement,
    LineController,
    BarController,
    PieController,
    RadarController,
    CategoryScale,
    LinearScale,
    TimeScale,
    Title,
    Tooltip,
    Legend,
    Filler,
    zoomPlugin,
    RadialLinearScale
);

export default function SensorDetail() {
    const { id: sensorId } = useParams();
    const { user, refreshToken } = useContext(AuthContext);
    const [loading, setLoading] = useState(true);
    const [graphData, setGraphData] = useState(null);
    const [tableData, setTableData] = useState([]);
    const [filteredTableData, setFilteredTableData] = useState([]);
    const [filterAnchor, setFilterAnchor] = useState(null);
    const [filterOption, setFilterOption] = useState('last7days');
    const [customStartDate, setCustomStartDate] = useState(null);
    const [customEndDate, setCustomEndDate] = useState(null);
    const [chartType, setChartType] = useState('line');
    const [currentView, setCurrentView] = useState('chart');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [error, setError] = useState('');
    const [lastMeasurementTime, setLastMeasurementTime] = useState(null);
    const [isOnline, setIsOnline] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [sortConfig, setSortConfig] = useState({ key: 'time', direction: 'desc' });

    useEffect(() => {
        fetchData();
    }, [sensorId, filterOption, customStartDate, customEndDate]);

    useEffect(() => {
        handleSearchAndSort();
    }, [tableData, searchTerm, sortConfig]);

    const fetchData = async () => {
        setLoading(true);
        try {
            let token = sessionStorage.getItem('accessToken');
            if (!token) token = await refreshToken();

            const startTime = getStartTime(filterOption);
            const endTime = getEndTime(filterOption);

            const response = await axios.post(
                `${process.env.REACT_APP_API_BASE_URL}/v1/iot-core/measurements-by-filter`,
                {
                    userId: user.userId,
                    measurement: sensorId,
                    startTime,
                    endTime
                },
                { headers: { Authorization: `Bearer ${token}` } }
            );

            const measurements = response.data;
            setTableData(measurements);

            const labels = measurements.map(m =>
                format(new Date(m.time), 'MMM d, yyyy, h:mm a')
            );
            const dataValues = measurements.map(m => m.value);
            const unit = measurements.length > 0 ? measurements[0].unit : '';

            setGraphData({
                labels,
                datasets: [
                    {
                        label: `${sensorId} (${unit})`,
                        data: dataValues,
                        borderColor: 'blue',
                        backgroundColor: 'rgba(0, 123, 255, 0.2)',
                        fill: true
                    }
                ]
            });

            if (measurements.length > 0) {
                const lastTime = new Date(measurements[measurements.length - 1].time);
                setIsOnline(new Date() - lastTime <= 5 * 60 * 1000);
                setLastMeasurementTime(lastTime);
            }
        } catch (error) {
            setError(error.message || 'Failed to fetch data');
            setSnackbarOpen(true);
        } finally {
            setLoading(false);
        }
    };

    const getStartTime = option => {
        const now = new Date();
        switch (option) {
            case 'last7days':
                return new Date(now.setDate(now.getDate() - 7)).toISOString();
            case 'last30days':
                return new Date(now.setDate(now.getDate() - 30)).toISOString();
            case 'last90days':
                return new Date(now.setDate(now.getDate() - 90)).toISOString();
            case 'custom':
                return customStartDate ? new Date(customStartDate).toISOString() : '';
            default:
                return '';
        }
    };

    const getEndTime = option => {
        return option === 'custom' && customEndDate ? new Date(customEndDate).toISOString() : new Date().toISOString();
    };

    const handleSearchAndSort = () => {
        let filtered = tableData.filter(row =>
            row.unit.toLowerCase().includes(searchTerm.toLowerCase()) ||
            row.value.toString().includes(searchTerm)
        );

        if (sortConfig.key) {
            filtered.sort((a, b) => {
                const aVal = a[sortConfig.key];
                const bVal = b[sortConfig.key];
                if (aVal < bVal) return sortConfig.direction === 'asc' ? -1 : 1;
                if (aVal > bVal) return sortConfig.direction === 'asc' ? 1 : -1;
                return 0;
            });
        }

        setFilteredTableData(filtered);
    };

    const handleSort = key => {
        setSortConfig(prev => ({
            key,
            direction: prev.key === key && prev.direction === 'asc' ? 'desc' : 'asc'
        }));
    };

    const createHistogramData = (data, binSize) => {
        const bins = {};
        data.forEach(value => {
            const bin = Math.floor(value / binSize) * binSize;
            bins[bin] = (bins[bin] || 0) + 1;
        });
        const labels = Object.keys(bins).map(bin => `${bin}-${+bin + binSize}`);
        const values = Object.values(bins);
        return { labels, values };
    };

    const getChartOptions = (type) => {
        const baseOptions = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                zoom: {
                    pan: { enabled: true, mode: 'xy' },
                    zoom: { wheel: { enabled: true }, pinch: { enabled: true }, mode: 'xy' }
                }
            }
        };

        const legendOptions = {
            pie: {
                position: 'top',
                align: 'center',
                labels: {
                    boxWidth: 20,
                    padding: 10,
                    font: { size: 12 }
                }
            },
            radar: {
                position: 'top',
                align: 'center',
                labels: {
                    boxWidth: 20,
                    padding: 10,
                    font: { size: 12 }
                }
            },
            default: {
                position: 'left',
                align: 'start'
            }
        };

        return {
            ...baseOptions,
            plugins: {
                ...baseOptions.plugins,
                legend: {
                    display: true,
                    ...(legendOptions[type] || legendOptions.default)
                }
            },
            layout: {
                padding: 20
            }
        };
    };

    if (loading) {
        return (
            <div style={{ textAlign: 'center' }}>
                <CircularProgress />
            </div>
        );
    }

    const histogramData = createHistogramData(graphData.datasets[0].data, 10);

    return (
        <LocalizationProvider dateAdapter={AdapterDateFns}>

        <Paper style={{ padding: 20 }}>
            <Grid container direction="column" alignItems="center" spacing={1}>
                <Grid item><Typography variant="h6">{sensorId}</Typography></Grid>
                <Grid item>
                    <SignalCellularAltIcon color={isOnline ? 'primary' : 'disabled'} fontSize="large" />
                </Grid>
                {lastMeasurementTime && (
                    <Grid item>
                        <Typography variant="body2">
                            Last data: {format(lastMeasurementTime, 'MMM d, yyyy, h:mm a')}
                        </Typography>
                    </Grid>
                )}
            </Grid>

            <Grid container alignItems="center" style={{ marginTop: 10 }}>
                <IconButton onClick={e => setFilterAnchor(e.currentTarget)}>
                    <FilterListIcon />
                </IconButton>
                <Popover
                    open={Boolean(filterAnchor)}
                    anchorEl={filterAnchor}
                    onClose={() => setFilterAnchor(null)}
                >
                    <Box sx={{ padding: 2, minWidth: 250 }}>
                        <TextField
                            label="Filter"
                            select
                            fullWidth
                            value={filterOption}
                            onChange={e => setFilterOption(e.target.value)}
                        >
                            <MenuItem value="last7days">Last 7 Days</MenuItem>
                            <MenuItem value="last30days">Last 30 Days</MenuItem>
                            <MenuItem value="last90days">Last 90 Days</MenuItem>
                            <MenuItem value="custom">Custom</MenuItem>
                        </TextField>
                        {filterOption === 'custom' && (
                            <>
                                <DateTimePicker
                                    label="Start Date"
                                    value={customStartDate}
                                    onChange={(newValue) => setCustomStartDate(newValue)}
                                    onError={(error) => console.error('Invalid start date:', error)}
                                    renderInput={(params) => <TextField {...params} fullWidth margin="normal" />}
                                />
                                <DateTimePicker
                                    label="End Date"
                                    value={customEndDate}
                                    onChange={(newValue) => setCustomEndDate(newValue)}
                                    onError={(error) => console.error('Invalid end date:', error)}
                                    renderInput={(params) => <TextField {...params} fullWidth margin="normal" />}
                                />
                            </>
                        )}
                    </Box>
                </Popover>
            </Grid>

            <Grid container justifyContent="center" spacing={2} style={{ marginTop: 10 }}>
                <MuiTooltip title="Table">
                    <IconButton color={currentView === 'table' ? 'primary' : 'default'} onClick={() => setCurrentView('table')}><TableChartIcon /></IconButton>
                </MuiTooltip>
                <MuiTooltip title="Chart">
                    <IconButton color={currentView === 'chart' ? 'primary' : 'default'} onClick={() => setCurrentView('chart')}><ShowChartIcon /></IconButton>
                </MuiTooltip>
                {currentView === 'chart' && (
                    <>
                        <MuiTooltip title="Line">
                            <IconButton color={chartType === 'line' ? 'primary' : 'default'} onClick={() => setChartType('line')}><TimelineIcon /></IconButton>
                        </MuiTooltip>
                        <MuiTooltip title="Bar">
                            <IconButton color={chartType === 'bar' ? 'primary' : 'default'} onClick={() => setChartType('bar')}><BarChartIcon /></IconButton>
                        </MuiTooltip>
                        <MuiTooltip title="Pie">
                            <IconButton color={chartType === 'pie' ? 'primary' : 'default'} onClick={() => setChartType('pie')}><PieChartIcon /></IconButton>
                        </MuiTooltip>
                        <MuiTooltip title="Radar">
                            <IconButton color={chartType === 'radar' ? 'primary' : 'default'} onClick={() => setChartType('radar')}><RadarIcon /></IconButton>
                        </MuiTooltip>
                        <MuiTooltip title="Histogram">
                            <IconButton color={chartType === 'histogram' ? 'primary' : 'default'} onClick={() => setChartType('histogram')}><BarChartIcon /></IconButton>
                        </MuiTooltip>
                    </>
                )}
            </Grid>

            {currentView === 'chart' && graphData && (
                <div style={{ height: 500, width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                    <div style={{ width: chartType === 'pie' || chartType === 'radar' ? '50%' : '80%', height: '100%' }}>
                        {chartType === 'line' && <Line data={graphData} options={getChartOptions('line')} />}
                        {chartType === 'bar' && <Bar data={graphData} options={getChartOptions('bar')} />}
                        {chartType === 'pie' && <Pie data={graphData} options={getChartOptions('pie')} />}
                        {chartType === 'radar' && <Radar data={graphData} options={getChartOptions('radar')} />}
                        {chartType === 'histogram' && (
                            <Bar
                                data={{
                                    labels: histogramData.labels,
                                    datasets: [
                                        {
                                            label: 'Frequency',
                                            data: histogramData.values,
                                            backgroundColor: 'rgba(0, 123, 255, 0.5)',
                                            borderColor: 'blue',
                                            borderWidth: 1
                                        }
                                    ]
                                }}
                                options={getChartOptions('bar')}
                            />
                        )}
                    </div>
                </div>
            )}

            {currentView === 'table' && (
                <>
                    <TextField
                        placeholder="Search by value or unit"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        fullWidth
                        margin="normal"
                        InputProps={{
                            startAdornment: (
                                <InputAdornment position="start">
                                    <SearchIcon />
                                </InputAdornment>
                            )
                        }}
                    />
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell onClick={() => handleSort('time')} style={{ cursor: 'pointer' }}>Date & Time</TableCell>
                                    <TableCell onClick={() => handleSort('value')} align="right" style={{ cursor: 'pointer' }}>Value</TableCell>
                                    <TableCell onClick={() => handleSort('unit')} align="right" style={{ cursor: 'pointer' }}>Unit</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {filteredTableData
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map((row, index) => (
                                        <TableRow key={index}>
                                            <TableCell>{format(new Date(row.time), 'MMM d, yyyy, h:mm a')}</TableCell>
                                            <TableCell align="right">{row.value}</TableCell>
                                            <TableCell align="right">{row.unit}</TableCell>
                                        </TableRow>
                                    ))}
                            </TableBody>
                        </Table>
                        <TablePagination
                            rowsPerPageOptions={[5, 10, 25]}
                            component="div"
                            count={filteredTableData.length}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={(e, newPage) => setPage(newPage)}
                            onRowsPerPageChange={(e) => setRowsPerPage(parseInt(e.target.value, 10))}
                        />
                    </TableContainer>
                </>
            )}

            <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={() => setSnackbarOpen(false)}>
                <Alert severity="error">{error}</Alert>
            </Snackbar>
        </Paper>
        </LocalizationProvider>

    );
}