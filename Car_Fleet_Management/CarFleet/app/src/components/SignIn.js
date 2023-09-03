import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import IconButton from '@mui/material/IconButton';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import InputAdornment from '@mui/material/InputAdornment';
import axios from 'axios';

function SignInSide() {
    // Getting the api url
    const apiUrl = process.env.REACT_APP_API_IP;

    const [openSuccessSnackbar, setOpenSuccessSnackbar] = React.useState(false);
    const [openErrorSnackbar, setOpenErrorSnackbar] = React.useState(false);

    // State variable to track password visibility
    const [showPassword, setShowPassword] = React.useState(false);

    // Toggle password visibility
    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword);
    };

    const handleSuccessSnackbarClose = () => {
        setOpenSuccessSnackbar(false);
    };

    const handleErrorSnackbarClose = () => {
        setOpenErrorSnackbar(false);
    };

    const showSuccessSnackbar = () => {
        setOpenSuccessSnackbar(true);
    };

    const showErrorSnackbar = () => {
        setOpenErrorSnackbar(true);
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

    // Check if the user's login credentials are stored in local storage
    const storedCredentials = localStorage.getItem('loginCredentials');
    const initialCredentials = storedCredentials
        ? JSON.parse(storedCredentials)
        : {
            email: '',
            password: '',
            rememberMe: false,
        };

    const [credentials, setCredentials] = React.useState(initialCredentials);

    const handleInputChange = (event) => {
        const { name, value, type, checked } = event.target;
        const newValue = type === 'checkbox' ? checked : value;
        setCredentials((prevCredentials) => ({
            ...prevCredentials,
            [name]: newValue,
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        const { email, password, rememberMe } = credentials;

        const requestBody = {
            username_email: email,
            password: password,
        };

        try {
            // Replace apiUrl + '/api/login' with the actual endpoint URL
            const response = await axios.post(apiUrl + '/api/login', requestBody);

            // Assuming the response contains user data as shown in the example
            const userData = response.data;
            console.log(userData);

            // Store the user's login credentials in local storage if "Remember me" is checked
            if (rememberMe) {
                localStorage.setItem('loginCredentials', JSON.stringify(credentials));
            } else {
                localStorage.removeItem('loginCredentials');
            }

            if (Object.keys(userData).length !== 0) {
                showSuccessSnackbar(); // Show success message
                // Store the user data in local storage
                localStorage.setItem('userData', JSON.stringify(userData));

                // Add a delay (e.g., 2 seconds) before redirection
                setTimeout(() => {
                    // Now, redirect to the dashboard page after successful login
                    window.location.href = '/CarFleet/Dashboard';
                }, 2000); // 2000 milliseconds = 2 seconds
            }
            else {
                showErrorSnackbar(); // Show error message
            }
        } catch (error) {
            // Handle error if the login request fails
            console.error('Error during login:', error);
            showErrorSnackbar(); // Show error message
        }
    };

    return (
        <ThemeProvider theme={theme}>
            <Grid container component="main" sx={{ height: '100vh' }}>
                <CssBaseline />
                <Grid
                    item
                    xs={false}
                    sm={4}
                    md={7}
                    sx={{
                        backgroundImage: 'url(https://source.unsplash.com/random?wallpapers)',
                        backgroundRepeat: 'no-repeat',
                        backgroundColor: (t) =>
                            t.palette.mode === 'light' ? t.palette.grey[50] : t.palette.grey[900],
                        backgroundSize: 'cover',
                        backgroundPosition: 'center',
                    }}
                />
                <Grid item xs={12} sm={8} md={5} component={Paper} elevation={6} square>
                    <Box
                        sx={{
                            my: 8,
                            mx: 4,
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                        }}
                    >
                        <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                            <LockOutlinedIcon />
                        </Avatar>
                        <Typography component="h1" variant="h5">
                            Car Fleet Management Application
                        </Typography>
                        <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 1 }}>
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
                                value={credentials.email}
                                onChange={handleInputChange}
                            />
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                name="password"
                                label="Password"
                                type={showPassword ? 'text' : 'password'}
                                id="password"
                                autoComplete="current-password"
                                variant="standard"
                                value={credentials.password}
                                onChange={handleInputChange}
                                InputProps={{
                                    endAdornment: (
                                        <InputAdornment position="end">
                                            <IconButton
                                                onClick={togglePasswordVisibility}
                                                edge="end"
                                            >
                                                {showPassword ? <VisibilityOff /> : <Visibility />}
                                            </IconButton>
                                        </InputAdornment>
                                    ),
                                }}
                            />
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        name="rememberMe"
                                        value={credentials.rememberMe}
                                        checked={credentials.rememberMe}
                                        color="primary"
                                        onChange={handleInputChange}
                                    />
                                }
                                label="Remember me"
                            />
                            <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
                                Sign In
                            </Button>
                            <Grid container>
                                <Grid item xs>
                                    <Link href="/CarFleet/ResetPassword" variant="body2">
                                        Forgot password?
                                    </Link>
                                </Grid>
                            </Grid>
                            <Box mt={5}>
                                <Typography variant="body2" color="text.secondary" align="center">
                                    {'Copyright © '}
                                    <Link color="inherit" href="http://sbapi.ddns.net">
                                        CarFleetManagement
                                    </Link>{' '}
                                    {new Date().getFullYear()}
                                    {'.'}
                                </Typography>
                            </Box>
                        </Box>
                    </Box>
                </Grid>
            </Grid>
            <Snackbar
                open={openSuccessSnackbar}
                autoHideDuration={5000}
                onClose={handleSuccessSnackbarClose}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
            >
                <Alert onClose={handleSuccessSnackbarClose} severity="success" sx={{ width: '100%' }}>
                    Login successful!
                </Alert>
            </Snackbar>
            <Snackbar
                open={openErrorSnackbar}
                autoHideDuration={5000}
                onClose={handleErrorSnackbarClose}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
            >
                <Alert onClose={handleErrorSnackbarClose} severity="error" sx={{ width: '100%' }}>
                    Login failed. Please check your credentials.
                </Alert>
            </Snackbar>
        </ThemeProvider>
    );
}

export default SignInSide;
