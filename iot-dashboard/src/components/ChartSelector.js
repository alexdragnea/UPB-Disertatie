// src/components/ChartSelector.js
import React from 'react';
import { MenuItem, Select, InputLabel, FormControl, Button } from '@mui/material';

export default function ChartSelector({ onAddChart }) {
    const [chartType, setChartType] = React.useState('');

    const handleAddChart = () => {
        if (chartType) {
            onAddChart(chartType);
            setChartType('');
        }
    };

    return (
        <FormControl fullWidth style={{ marginBottom: 20 }}>
            <InputLabel id="chart-select-label">Select Chart Type</InputLabel>
            <Select
                labelId="chart-select-label"
                value={chartType}
                onChange={(e) => setChartType(e.target.value)}
                label="Select Chart Type"
            >
                <MenuItem value="line">Line Chart</MenuItem>
                <MenuItem value="bar">Bar Chart</MenuItem>
                <MenuItem value="pie">Pie Chart</MenuItem>
            </Select>
            <Button
                variant="contained"
                color="primary"
                style={{ marginTop: 10 }}
                onClick={handleAddChart}
            >
                Add Chart
            </Button>
        </FormControl>
    );
}
