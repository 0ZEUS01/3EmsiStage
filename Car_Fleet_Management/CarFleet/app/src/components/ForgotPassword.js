import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import axios from 'axios';

function Copyright(props) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            {'Copyright © '}
            <Link color="inherit" href="http://sbapi.ddns.net">
                CarFleetManagement
            </Link>{' '}
            {new Date().getFullYear()}
            {'.'}
        </Typography>
    );
}

export default function SignIn() {
    // Getting the api url
    const apiUrl = process.env.REACT_APP_API_IP;

    const [openErrorSnackbar, setOpenErrorSnackbar] = React.useState(false);
    const [snackbarMessage, setSnackbarMessage] = React.useState('');
    const [snackbarSeverity, setSnackbarSeverity] = React.useState('error'); // Default to 'error' for error messages


    const openSnackbar = (message) => {
        setSnackbarMessage(message);
        setOpenErrorSnackbar(true);
    };

    const handleSnackbarClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }

        setOpenErrorSnackbar(false);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);
        const email = data.get('email');

        // Replace {username_email} with the actual email from the form
        const url = apiUrl + `/api/users/${email}/recover`;

        try {
            const response = await axios.get(url);

            if (response.status === 200 && response.data === 1) {
                // Request was successful
                console.log('Password recovery request successful');
                setSnackbarSeverity('success'); // Set the severity to 'success'
                openSnackbar('Password recovery successful');

                // Add a delay (e.g., 2 seconds) before redirection
                setTimeout(() => {
                    // Now, redirect to the dashboard page after successful login
                    window.location.href = '/CarFleet/Login';
                }, 2000); // 2000 milliseconds = 2 seconds

                // You might want to redirect or show a success message here
            } else if (response.status === 200 && response.data === -1) {
                // Request failed
                console.error('Password recovery request failed');
                openSnackbar('Password recovery failed');
                // You might want to display an error message to the user
            } else {
                // Handle other status codes or unexpected responses here
                console.error('Unexpected response:', response);
                openSnackbar('An unexpected error occurred');
            }
        } catch (error) {
            console.error('An error occurred:', error);
            openSnackbar('An error occurred while processing your request');
            // Handle any network or other errors that occurred during the request
        }
    };


    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');


    const theme = React.useMemo(
        () =>
            createTheme({
                palette: {
                    mode: prefersDarkMode ? 'dark' : 'light',
                },
            }),
        [prefersDarkMode],
    );


    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="xs">
                <CssBaseline />
                <Box
                    sx={{
                        marginTop: 8,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                        <LockOutlinedIcon />
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        Forgot Password
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="email"
                            label="Email Address / Username"
                            name="email"
                            autoComplete="email"
                            variant="standard"
                            autoFocus
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            Get New Password
                        </Button>
                        <Grid container>
                            <Grid item xs>
                                <Link href="/CarFleet/Login" variant="body2">
                                    You Remember Your Old Password ?
                                </Link>
                            </Grid>
                        </Grid>
                    </Box>
                </Box>
                <Copyright sx={{ mt: 8, mb: 4 }} />
            </Container>
            <Snackbar
                open={openErrorSnackbar}
                autoHideDuration={5000} // Adjust the duration as needed
                onClose={handleSnackbarClose}
                anchorOrigin={{
                    vertical: 'top',
                    horizontal: 'center',
                }}
            >
                <MuiAlert onClose={handleSnackbarClose} severity={snackbarSeverity} elevation={6} variant="filled">
                    {snackbarMessage}
                </MuiAlert>
            </Snackbar>
        </ThemeProvider>
    );
}