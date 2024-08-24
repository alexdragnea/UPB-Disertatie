// src/components/Dashboard.js
import React, { useState } from 'react';
import { Grid, Paper } from '@mui/material';
import ChartSelector from './ChartSelector';
import LineChartComponent from './LineChartComponent';
import BarChartComponent from './BarChartComponent';
import PieChartComponent from './PieChartComponent';

const chartComponents = {
    line: LineChartComponent,
    bar: BarChartComponent,
    pie: PieChartComponent,
};

export default function Dashboard() {
    const [charts, setCharts] = useState([]);

    const handleAddChart = (type) => {
        setCharts([...charts, type]);
    };

    return (
        <div>
            <ChartSelector onAddChart={handleAddChart} />
            <Grid container spacing={3}>
                {charts.map((chartType, index) => {
                    const ChartComponent = chartComponents[chartType];
                    return (
                        <Grid item xs={12} md={6} key={index}>
                            <Paper style={{ padding: 20 }}>
                                <ChartComponent />
                            </Paper>
                        </Grid>
                    );
                })}
            </Grid>
        </div>
    );
}
