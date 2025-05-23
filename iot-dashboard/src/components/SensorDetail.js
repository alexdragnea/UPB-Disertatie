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
    Tooltip as MuiTooltip
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
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
import DateTimeFilter from "./DateTimeFilter";
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
    const [filterOption, setFilterOption] = useState('last7days');
    const [customStartDate, setCustomStartDate] = useState(null);
    const [customEndDate, setCustomEndDate] = useState(null);
    const [chartType, setChartType] = useState(() => localStorage.getItem('chartType') || 'line');
    const [currentView, setCurrentView] = useState(() => localStorage.getItem('currentView') || 'chart');    
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
        localStorage.setItem('chartType', chartType);
    }, [chartType]);
    
    useEffect(() => {
        localStorage.setItem('currentView', currentView);
    }, [currentView]);

    useEffect(() => {
        handleSearchAndSort();
    }, [tableData, searchTerm, sortConfig]);

    const fetchData = async () => {
        // Prevent API call if custom filter is chosen but no date is selected
        if (filterOption === 'custom' && (!customStartDate || !customEndDate)) {
            return;
        }

        setGraphData(null);
        setTableData([]);
        setFilteredTableData([]);
        setLoading(true);


        try {
            let token = sessionStorage.getItem('accessToken');
            if (!token) token = await refreshToken();

            const startTime = getStartTime(filterOption);
            const endTime = getEndTime(filterOption);

            const response = await axios.post(
                `${process.env.REACT_APP_API_BASE_URL}/v1/iot-core/measurements-by-filter?cacheBust=${new Date().getTime()}`,
                {
                    userId: user.userId,
                    measurement: sensorId,
                    startTime,
                    endTime
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Cache-Control': 'no-cache',
                        Pragma: 'no-cache'
                    }
                }
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

    const getStartTime = (option) => {
        const now = new Date();
        switch (option) {
            case 'last1hour':
                return new Date(now.getTime() - 1 * 60 * 60 * 1000).toISOString();
            case 'last24hours':
                return new Date(now.getTime() - 24 * 60 * 60 * 1000).toISOString();
            case 'lastweek':
                return new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000).toISOString();
            case 'last7days':
                return new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000).toISOString();
            case 'custom':
                return customStartDate ? new Date(customStartDate).toISOString() : now.toISOString();
            default:
                return now.toISOString();
        }
    };

    const getEndTime = (option) => {
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

    const histogramData = graphData && graphData.datasets && graphData.datasets[0]
        ? createHistogramData(graphData.datasets[0].data, 10)
        : { labels: [], values: [] };

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
                    {tableData.length === 0 && !loading && (
                        <Grid item>
                            <Typography variant="body2" color="textSecondary">
                                No data available for the selected period.
                            </Typography>
                        </Grid>
                    )}

                </Grid>

                <Grid container alignItems="center" style={{ marginTop: 10 }}>
                    <DateTimeFilter
                        filterOption={filterOption}
                        setFilterOption={setFilterOption}
                        customStartDate={customStartDate}
                        setCustomStartDate={setCustomStartDate}
                        customEndDate={customEndDate}
                        setCustomEndDate={setCustomEndDate}
                    />
                    <MuiTooltip title="Refresh">
                        <IconButton onClick={fetchData}>
                            <RefreshIcon />
                        </IconButton>
                    </MuiTooltip>
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

                {currentView === 'chart' && graphData && graphData.datasets && (
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
