// src/components/PieChartComponent.js
import React from 'react';
import { PieChart, Pie, Tooltip, ResponsiveContainer } from 'recharts';

const data = [
    { name: 'Group A', value: 400 },
    { name: 'Group B', value: 300 },
    { name: 'Group C', value: 300 },
    { name: 'Group D', value: 200 },
];

export default function PieChartComponent() {
    return (
        <ResponsiveContainer width="100%" height={400}>
            <PieChart>
                <Pie data={data} dataKey="value" fill="#8884d8" label />
                <Tooltip />
            </PieChart>
        </ResponsiveContainer>
    );
}
