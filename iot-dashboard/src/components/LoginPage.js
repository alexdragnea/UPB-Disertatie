import React, { useState, useContext } from 'react';
import { TextField, Button, Paper, Grid, Snackbar, Alert, CircularProgress, Typography, Box, FormControlLabel, Checkbox, Link as MuiLink } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import AuthContext from '../AuthContext'; // Adjust the import based on your structure
import logo from '../assets/img/icons/iot-logo.png'; // Add a logo
import '../assets/css/LoginPage.css'; // Import custom CSS

const API_URL = process.env.REACT_APP_API_URL || 'https://localhost:8888/v1/iot-user';

export default function LoginPage() {
    const { login } = useContext(AuthContext); // Get login function from context
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [rememberMe, setRememberMe] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false); // Loader state
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true); // Start loading

        try {
            const response = await fetch(`${API_URL}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password, rememberMe }),
            });

            if (response.ok) {
                const data = await response.json();
                login(data.accessToken); // Update context state with login function
                setMessage('Login successful!');
                setEmail('');
                setPassword('');
                navigate('/'); // Redirect to home page
            } else {
                const errorData = await response.json();
                // Handle specific error messages
                if (response.status === 401) {
                    setError('Invalid email or password.');
                } else {
                    setError(errorData.message || 'Login failed.');
                }
            }
        } catch (error) {
            setError('An unexpected error occurred.');
        } finally {
            setLoading(false); // Stop loading
        }
    };

    return (
        <Box className="login-container">
            <Paper elevation={6} className="login-form">
                <Box className="login-header">
                    <img src={logo} alt="IoT Dashboard Logo" className="login-logo" />
                    <Typography variant="h5" component="h1" gutterBottom>
                        IoT Dashboard Login
                    </Typography>
                </Box>
                <form onSubmit={handleLogin}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <TextField
                                label="Email"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                label="Password"
                                type="password"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        checked={rememberMe}
                                        onChange={(e) => setRememberMe(e.target.checked)}
                                        color="primary"
                                    />
                                }
                                label="Remember Me"
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <Button
                                type="submit"
                                variant="contained"
                                color="primary"
                                fullWidth
                                disabled={loading} // Disable button during loading
                                sx={{ marginTop: 2 }}
                            >
                                {loading ? <CircularProgress size={24} /> : 'Login'}
                            </Button>
                        </Grid>
                        <Grid item xs={12}>
                            <Button
                                component={Link}
                                to="/register"
                                variant="outlined"
                                color="primary"
                                fullWidth
                                sx={{ marginTop: 2 }}
                            >
                                Register
                            </Button>
                        </Grid>
                    </Grid>
                </form>

                <Snackbar open={!!message} autoHideDuration={6000} onClose={() => setMessage('')}>
                    <Alert onClose={() => setMessage('')} severity="success" className="snackbar-alert">
                        {message}
                    </Alert>
                </Snackbar>

                <Snackbar open={!!error} autoHideDuration={6000} onClose={() => setError('')}>
                    <Alert onClose={() => setError('')} severity="error" className="snackbar-alert">
                        {error}
                    </Alert>
                </Snackbar>
            </Paper>
        </Box>
    );
}