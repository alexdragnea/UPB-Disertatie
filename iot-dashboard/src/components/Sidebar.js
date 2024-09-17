import React, { useState } from 'react';
import {
    Drawer, List, ListItem, ListItemIcon, ListItemText, Collapse, Button
} from '@mui/material';
import DashboardIcon from '@mui/icons-material/Dashboard';
import DevicesIcon from '@mui/icons-material/Devices';
import AddBoxIcon from '@mui/icons-material/AddBox';
import ExpandLess from '@mui/icons-material/ExpandLess';
import ExpandMore from '@mui/icons-material/ExpandMore';
import { Link } from 'react-router-dom'; // For routing links

const drawerWidth = 240;

export default function Sidebar({ devices = [] }) { // Default to empty array
    const [openDevices, setOpenDevices] = useState(true); // Expanded by default

    const toggleDevices = () => {
        setOpenDevices(!openDevices); // Toggle expand/collapse
    };

    return (
        <Drawer
            variant="permanent"
            sx={{
                width: drawerWidth,
                flexShrink: 0,
                '& .MuiDrawer-paper': {
                    width: drawerWidth,
                    boxSizing: 'border-box',
                    top: '64px',
                    height: 'calc(100vh - 64px)',
                    borderRight: '1px solid #ddd',
                },
            }}
        >
            <List>
                {/* Dashboard Link */}
                <ListItem button component={Link} to="/admin/dashboard">
                    <ListItemIcon><DashboardIcon /></ListItemIcon>
                    <ListItemText primary="Dashboard" />
                </ListItem>

                {/* Devices Section */}
                <ListItem button onClick={toggleDevices}>
                    <ListItemIcon><DevicesIcon /></ListItemIcon>
                    <ListItemText primary="Devices" />
                    {openDevices ? <ExpandLess /> : <ExpandMore />}
                </ListItem>
                
                {/* Collapsible List of Devices */}
                <Collapse in={openDevices} timeout="auto" unmountOnExit>
                    <List component="div" disablePadding>
                        {devices.map((device) => (
                            <ListItem
                                key={device.id}
                                button
                                component={Link}
                                to={`/admin/devices/${device.id}`}
                                sx={{ pl: 4 }} // Indentation for nested items
                            >
                                <ListItemText primary={device.name} />
                            </ListItem>
                        ))}
                    </List>
                </Collapse>

                {/* Add Device Link */}
                <ListItem button component={Link} to="/admin/add-device">
                    <ListItemIcon><AddBoxIcon /></ListItemIcon>
                    <ListItemText primary="Add Device" />
                </ListItem>

            </List>
        </Drawer>
    );
}
