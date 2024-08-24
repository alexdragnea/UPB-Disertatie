// src/components/Sidebar.js
import React from 'react';
import { Drawer, List, ListItem, ListItemIcon, ListItemText } from '@mui/material';
import DashboardIcon from '@mui/icons-material/Dashboard';
import DevicesIcon from '@mui/icons-material/Devices';
import AddBoxIcon from '@mui/icons-material/AddBox';

const drawerWidth = 240; // Adjust as needed

export default function Sidebar() {
    return (
        <Drawer
            variant="permanent"
            sx={{
                width: drawerWidth,
                flexShrink: 0,
                '& .MuiDrawer-paper': {
                    width: drawerWidth,
                    boxSizing: 'border-box',
                    top: '64px', // Adjust top position for spacing from the top
                    height: 'calc(100vh - 64px)', // Adjust height to ensure it fits the screen
                    borderRight: '1px solid #ddd', // Light border for separation
                },
            }}
        >
            <List>
                <ListItem button component="a" href="/admin/dashboard">
                    <ListItemIcon><DashboardIcon /></ListItemIcon>
                    <ListItemText primary="Dashboard" />
                </ListItem>
                <ListItem button component="a" href="/admin/devices">
                    <ListItemIcon><DevicesIcon /></ListItemIcon>
                    <ListItemText primary="Devices" />
                </ListItem>
                <ListItem button component="a" href="/admin/add-device">
                    <ListItemIcon><AddBoxIcon /></ListItemIcon>
                    <ListItemText primary="Add Device" />
                </ListItem>
            </List>
        </Drawer>
    );
}
