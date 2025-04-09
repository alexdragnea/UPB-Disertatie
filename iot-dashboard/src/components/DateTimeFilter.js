import React, { useState } from 'react';
import {
    IconButton,
    TextField,
    MenuItem,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Grid
} from '@mui/material';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import FilterListIcon from '@mui/icons-material/FilterList';

export default function DateTimeFilter({
                                           filterOption,
                                           setFilterOption,
                                           customStartDate,
                                           setCustomStartDate,
                                           customEndDate,
                                           setCustomEndDate
                                       }) {
    const [dialogOpen, setDialogOpen] = useState(false);

    const handleFilterChange = (option) => {
        setFilterOption(option);
        if (option !== 'custom') {
            setDialogOpen(false); // Close dialog if a preset option is selected
        }
    };

    const isValidCustomFilter = () => {
        return customStartDate && customEndDate && customStartDate < customEndDate;
    };

    const handleApply = () => {
        if (isValidCustomFilter()) {
            setFilterOption('custom'); // Set filter to custom if applied correctly
            setDialogOpen(false);
        } else {
            alert("Please select a valid start and end date range.");
        }
    };

    return (
        <>
            <IconButton onClick={() => setDialogOpen(true)}>
                <FilterListIcon />
            </IconButton>
            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth maxWidth="sm">
                <DialogTitle>Filter</DialogTitle>
                <DialogContent>
                    <TextField
                        label="Filter"
                        select
                        fullWidth
                        value={filterOption}
                        onChange={(e) => handleFilterChange(e.target.value)}
                        margin="normal"
                    >
                        <MenuItem value="last1hour">Last 1 Hour</MenuItem>
                        <MenuItem value="last24hours">Last 24 Hours</MenuItem>
                        <MenuItem value="last7days">Last 7 Days</MenuItem>
                        <MenuItem value="custom">Custom</MenuItem>
                    </TextField>
                    {filterOption === 'custom' && (
                        <Grid container spacing={2} style={{ marginTop: 10 }}>
                            <Grid item xs={6}>
                                <DateTimePicker
                                    label="Start Date"
                                    value={customStartDate}
                                    onChange={(newValue) => setCustomStartDate(newValue)}
                                    renderInput={(params) => <TextField {...params} fullWidth />}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <DateTimePicker
                                    label="End Date"
                                    value={customEndDate}
                                    onChange={(newValue) => setCustomEndDate(newValue)}
                                    renderInput={(params) => <TextField {...params} fullWidth />}
                                />
                            </Grid>
                        </Grid>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
                    <Button
                        onClick={handleApply}
                        variant="contained"
                        color="primary"
                        disabled={!isValidCustomFilter()} // Disable button until valid
                    >
                        Apply
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
}
