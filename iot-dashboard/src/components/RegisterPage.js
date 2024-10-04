import React, { useState } from 'react';
import { TextField, Button, Paper, Grid, Snackbar, Alert } from '@mui/material';
import { Link } from 'react-router-dom';

const API_URL = 'https://localhost:8888/v1/iot-user';

export default function RegisterPage() {
    const [email, setEmail] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleRegister = async (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            setError('Passwords do not match.');
            return;
        }

        try {
            const response = await fetch(`${API_URL}/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, firstName, lastName, password }),
            });

            if (response.ok) {
                setMessage('Registration successful! Please log in.');
                setEmail('');
                setFirstName('');
                setLastName('');
                setPassword('');
                setConfirmPassword('');
            } else {
                const errorData = await response.json();
                setError(errorData.message || 'Registration failed.');
            }
        } catch (error) {
            console.error('Registration error:', error);
            setError('An unexpected error occurred.');
        }
    };

    return (
        <Paper style={{ padding: 20 }}>
            <h2>Register</h2>
            <form>
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
                            label="First Name"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                            required
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            label="Last Name"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
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
                        <TextField
                            label="Confirm Password"
                            type="password"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <Button
                            onClick={handleRegister}
                            variant="contained"
                            color="primary"
                            style={{ marginTop: 20 }}
                        >
                            Register
                        </Button>
                        <Button
                            component={Link}
                            to="/login"
                            variant="outlined"
                            color="primary"
                            style={{ marginTop: 20, marginLeft: 10 }}
                        >
                            Back to Login
                        </Button>
                    </Grid>
                </Grid>
            </form>

            {/* Success Notification */}
            <Snackbar open={!!message} autoHideDuration={6000} onClose={() => setMessage('')}>
                <Alert onClose={() => setMessage('')} severity="success" sx={{ width: '100%' }}>
                    {message}
                </Alert>
            </Snackbar>

            {/* Error Notification */}
            <Snackbar open={!!error} autoHideDuration={6000} onClose={() => setError('')}>
                <Alert onClose={() => setError('')} severity="error" sx={{ width: '100%' }}>
                    {error}
                </Alert>
            </Snackbar>
        </Paper>
    );
}
