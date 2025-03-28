import React, { useState } from 'react';
import { AppBar, Toolbar, Button, Typography, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import '../assets/css/Header.styles.css';

const Header = ({ onLogout, isAuthenticated }) => {
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);

    const handleProfileClick = () => {
        navigate('/profile');
    };

    const handleTitleClick = () => {
        navigate('/');
    };

    const handleLogoutClick = () => {
        setOpen(true);
    };

    const handleCloseDialog = () => {
        setOpen(false);
    };

    const handleConfirmLogout = () => {
        onLogout();
        handleCloseDialog();
    };

    return (
        <AppBar position="static" className="header">
            <Toolbar className="toolbar">
                <Typography
                    variant="h6"
                    className="title"
                    onClick={handleTitleClick}
                >
                    IoT Dashboard
                </Typography>
                {isAuthenticated && (
                    <div className="nav-buttons">
                        <Button
                            color="inherit"
                            onClick={handleProfileClick}
                            className="nav-link"
                            startIcon={<AccountCircleIcon />}
                        >
                            Profile
                        </Button>
                        <Button
                            color="inherit"
                            onClick={handleLogoutClick}
                            className="nav-link"
                            startIcon={<ExitToAppIcon />}
                        >
                            Logout
                        </Button>
                    </div>
                )}
            </Toolbar>

            <Dialog open={open} onClose={handleCloseDialog}>
                <DialogTitle>Confirm Logout</DialogTitle>
                <DialogContent>
                    Are you sure you want to log out?
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog} color="primary">Cancel</Button>
                    <Button onClick={handleConfirmLogout} color="primary">Logout</Button>
                </DialogActions>
            </Dialog>
        </AppBar>
    );
};

export default Header;
