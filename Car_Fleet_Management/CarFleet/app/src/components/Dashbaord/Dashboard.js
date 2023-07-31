import * as React from 'react';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import ResponsiveAppBar from './navBar';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import { CardActionArea } from '@mui/material';
import CardMedia from '@mui/material/CardMedia';

function Dashboard() {
    // Hook to check if the user prefers dark mode
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');

    // Creating the theme based on the user's preference
    const theme = React.useMemo(
        () =>
            createTheme({
                palette: {
                    mode: prefersDarkMode ? 'dark' : 'light',
                },
            }),
        [prefersDarkMode],
    );

    // State to store the number of cars and admins
    const [numberOfCars, setNumberOfCars] = React.useState(0);
    // State to store the number of users
    const [numberOfUsers, setNumberOfUsers] = React.useState(0);
    
    // Function to fetch the number of cars from the API
    React.useEffect(() => {
        const fetchCarsData = async () => {
            try {
                const response = await fetch('http://sbapi.ddns.net:8082/api/cars');
                if (response.ok) {
                    const data = await response.json();
                    // Assuming the API response returns a list of cars, we calculate the number of cars based on the array length
                    setNumberOfCars(data.length);
                } else {
                    console.error('Failed to fetch car data from API');
                }
            } catch (error) {
                console.error('Error fetching car data:', error);
            }
        };
        fetchCarsData();
    }, []);

    // Function to fetch the user data from the API
    React.useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch('http://sbapi.ddns.net:8082/api/users');
                if (response.ok) {
                    const data = await response.json();
                    // Calculate the number of users based on the array length
                    setNumberOfUsers(data.length);
                } else {
                    console.error('Failed to fetch user data from API');
                }
            } catch (error) {
                console.error('Error fetching user data:', error);
            }
        };
        fetchData();
    }, []);

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            {/* Rendering the NavBar component */}
            <ResponsiveAppBar />

            <br />

            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                <Grid container spacing={1} justifyContent="center">
                    <Grid item xs={12} md={4}>
                        {/* Use xs={12} to take full width on small screens and md={4} to take one-third width on medium screens and larger */}
                        <Card>
                            <CardActionArea>
                                <CardMedia
                                    component="img"
                                    height="200"
                                    image="https://clipart-library.com/images/kcKnqoK6i.png"
                                />
                                <CardContent>
                                    <Typography gutterBottom variant="h5" component="div">
                                        Numbers of cars: {numberOfCars}
                                    </Typography>
                                </CardContent>
                            </CardActionArea>
                        </Card>
                    </Grid>
                    <Grid item xs={12} md={4}>
                        {/* Use xs={12} to take full width on small screens and md={4} to take one-third width on medium screens and larger */}
                        <Card>
                            <CardActionArea>
                                <CardMedia
                                    component="img"
                                    height="200"
                                    image="https://cdn-icons-png.flaticon.com/512/2302/2302386.png"
                                />
                                <CardContent>
                                    <Typography gutterBottom variant="h5" component="div">
                                        Numbers of users: {numberOfUsers}
                                    </Typography>
                                </CardContent>
                            </CardActionArea>
                        </Card>
                    </Grid>
                </Grid>
            </div>
        </ThemeProvider>
    );
}

export default Dashboard;
