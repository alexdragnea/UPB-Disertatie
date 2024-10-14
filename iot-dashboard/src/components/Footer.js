import React from 'react';
import { Box, Typography } from '@mui/material';

const Footer = () => {
    return (
        <Box component="footer" sx={{ 
            textAlign: 'center', 
            padding: '10px 0', 
            backgroundColor: '#1976d2', // Your preferred color
            color: '#fff', // White text for contrast
            marginTop: 'auto' // Ensures footer sticks to the bottom
        }}>
            <Typography variant="body2">
                © 2024 Iot UPB Dashboard.
            </Typography>
        </Box>
    );
};

export default Footer;
