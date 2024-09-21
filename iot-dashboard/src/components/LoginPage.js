import React, { useState, useContext } from 'react';
import { TextField, Button, Paper, Grid, Snackbar, Alert } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import AuthContext from '../AuthContext'; // Adjust the import based on your structure

const API_URL = 'http://localhost:8888/v1/iot-user';

export default function LoginPage() {
    const { login } = useContext(AuthContext); // Get login function from context
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch(`${API_URL}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
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
                setError(errorData.message || 'Login failed.');
            }
        } catch (error) {
            setError('An unexpected error occurred.');
        }
    };

    return (
        <Paper style={{ padding: 20 }}>
            <h2>Login</h2>
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
                        <Button
                            type="submit"
                            variant="contained"
                            color="primary"
                            style={{ marginTop: 20 }}
                        >
                            Login
                        </Button>
                        <Button
                            component={Link}
                            to="/register"
                            variant="outlined"
                            color="primary"
                            style={{ marginTop: 20, marginLeft: 10 }}
                        >
                            Register
                        </Button>
                    </Grid>
                </Grid>
            </form>

            <Snackbar open={!!message} autoHideDuration={6000} onClose={() => setMessage('')}>
                <Alert onClose={() => setMessage('')} severity="success" sx={{ width: '100%' }}>
                    {message}
                </Alert>
            </Snackbar>

            <Snackbar open={!!error} autoHideDuration={6000} onClose={() => setError('')}>
                <Alert onClose={() => setError('')} severity="error" sx={{ width: '100%' }}>
                    {error}
                </Alert>
            </Snackbar>
        </Paper>
    );
}
