import React, { useState } from 'react';
import { TextField, Button, Paper } from '@mui/material';

export default function Profile() {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const handlePasswordChange = (e) => {
        e.preventDefault();
        if (newPassword !== confirmPassword) {
            alert("New password and confirm password don't match");
            return;
        }
        // Handle password change logic here
        console.log('Password changed successfully');
    };

    return (
        <Paper style={{ padding: 20 }}>
            <h2>Profile</h2>
            <form onSubmit={handlePasswordChange}>
                <TextField
                    label="Old Password"
                    type="password"
                    fullWidth
                    margin="normal"
                    value={oldPassword}
                    onChange={(e) => setOldPassword(e.target.value)}
                />
                <TextField
                    label="New Password"
                    type="password"
                    fullWidth
                    margin="normal"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                />
                <TextField
                    label="Confirm New Password"
                    type="password"
                    fullWidth
                    margin="normal"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                />
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    style={{ marginTop: 20 }}
                >
                    Change Password
                </Button>
            </form>
        </Paper>
    );
}
