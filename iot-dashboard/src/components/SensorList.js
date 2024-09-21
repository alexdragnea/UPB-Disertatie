import React, { useState, useEffect } from 'react';
import { List, ListItem, ListItemText, Paper } from '@mui/material';
import axios from 'axios';  // Assuming axios is used for API requests

export default function SensorList() {
    const [sensors, setSensors] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Fetching sensor data from the API
        axios.get('/api/sensors') // Update with your actual API endpoint
            .then((response) => {
                const data = response.data; // Assuming data is in the form of UserMeasurementDto
                setSensors(data.measurements); // Update with correct field from UserMeasurementDto
                setLoading(false);
            })
            .catch((error) => {
                console.error('Error fetching sensors:', error);
                setLoading(false);
            });
    }, []);

    if (loading) {
        return <div>Loading sensors...</div>;
    }

    return (
        <Paper style={{ padding: 20 }}>
            <h2>Sensor List</h2>
            <List>
                {sensors.map((sensor, index) => (
                    <ListItem button key={index} component="a" href={`/sensors/${sensor}`}>
                        <ListItemText
                            primary={sensor} // Displaying sensor name
                        />
                    </ListItem>
                ))}
            </List>
        </Paper>
    );
}
