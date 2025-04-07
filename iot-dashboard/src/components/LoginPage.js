import React, { useState, useContext } from 'react';
import { TextField, Button, Paper, Grid, Snackbar, Alert, CircularProgress, Typography, Box, FormControlLabel, Checkbox } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import AuthContext from '../AuthContext';
import logo from '../assets/img/icons/iot-logo.png';
import '../assets/css/LoginPage.css';

const API_URL = `${process.env.REACT_APP_API_BASE_URL}/v1/iot-user`;

export default function LoginPage() {
    const { login } = useContext(AuthContext);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [rememberMe, setRememberMe] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await fetch(`${API_URL}/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password, rememberMe }),
            });

            if (response.ok) {
                const data = await response.json();
                login(data.accessToken);
                setMessage('Login successful!');
                setEmail('');
                setPassword('');
                navigate('/');
            } else {
                const errorData = await response.json();
                setError(response.status === 401 ? 'Invalid email or password.' : errorData.message || 'Login failed.');
            }
        } catch {
            setError('An unexpected error occurred.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box className="login-container">
            <Paper elevation={6} className="login-form">
                <Box textAlign="center" mb={2}>
                    <img src={logo} alt="IoT Dashboard Logo" className="login-logo" />
                    <Typography variant="h5">IoT Dashboard Login</Typography>
                </Box>
                <form onSubmit={handleLogin}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <TextField label="Email" variant="outlined" fullWidth value={email} onChange={(e) => setEmail(e.target.value)} required />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField label="Password" type="password" variant="outlined" fullWidth value={password} onChange={(e) => setPassword(e.target.value)} required />
                        </Grid>
                        <Grid item xs={12}>
                            <FormControlLabel control={<Checkbox checked={rememberMe} onChange={(e) => setRememberMe(e.target.checked)} />} label="Remember Me" />
                        </Grid>
                        <Grid item xs={12}>
                            <Button type="submit" variant="contained" color="primary" fullWidth disabled={loading}>{loading ? <CircularProgress size={24} /> : 'Login'}</Button>
                        </Grid>
                        <Grid item xs={12}>
                            <Button component={Link} to="/register" variant="outlined" color="primary" fullWidth>Register</Button>
                        </Grid>
                    </Grid>
                </form>
                <Snackbar open={!!message} autoHideDuration={6000} onClose={() => setMessage('')}><Alert onClose={() => setMessage('')} severity="success">{message}</Alert></Snackbar>
                <Snackbar open={!!error} autoHideDuration={6000} onClose={() => setError('')}><Alert onClose={() => setError('')} severity="error">{error}</Alert></Snackbar>
            </Paper>
        </Box>
    );
}