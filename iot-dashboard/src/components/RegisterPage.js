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
    const [validationErrors, setValidationErrors] = useState({});

    const validateEmail = (email) => {
        // Basic email regex for validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };

    const handleRegister = async (e) => {
        e.preventDefault();

        // Clear previous validation errors
        setValidationErrors({});
        let errors = {};

        // Validate email
        if (!validateEmail(email)) {
            errors.email = 'Invalid email format.';
        }

        // Check required fields
        if (!firstName) {
            errors.firstName = 'First Name is required.';
        }

        if (!lastName) {
            errors.lastName = 'Last Name is required.';
        }

        // Check if password is not empty
        if (!password) {
            errors.password = 'Password is required.';
        }

        // Check if passwords match
        if (password !== confirmPassword) {
            errors.confirmPassword = 'Passwords do not match.';
        }

        // If there are validation errors, set them and return early
        if (Object.keys(errors).length > 0) {
            setValidationErrors(errors);
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
                            error={!!validationErrors.email}
                            helperText={validationErrors.email}
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
                            error={!!validationErrors.firstName}
                            helperText={validationErrors.firstName}
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
                            error={!!validationErrors.lastName}
                            helperText={validationErrors.lastName}
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
                            error={!!validationErrors.password}
                            helperText={validationErrors.password}
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
                            error={!!validationErrors.confirmPassword}
                            helperText={validationErrors.confirmPassword}
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
