// src/components/DeviceList.js
import React from 'react';
import { List, ListItem, ListItemText, Paper } from '@mui/material';

export default function DeviceList({ devices }) {
    return (
        <Paper style={{ padding: 20 }}>
            <h2>Device List</h2>
            <List>
                {devices.map((device) => (
                    <ListItem button key={device.id} component="a" href={`/admin/devices/${device.id}`}>
                        <ListItemText primary={device.name} secondary={device.type} />
                    </ListItem>
                ))}
            </List>
        </Paper>
    );
}
