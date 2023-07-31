import * as React from 'react';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import ResponsiveAppBar from './navBar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import axios from 'axios';
import { DataGrid } from '@mui/x-data-grid';
import Swal from "sweetalert2";

function Cars() {
  const [cars, setCars] = React.useState([]); // State to store the car data

  React.useEffect(() => {
    // Make the GET request using Axios
    axios.get('http://sbapi.ddns.net:8082/api/cars')
      .then((response) => {
        // Handle success and set the retrieved data to the state
        setCars(response.data);
      })
      .catch((error) => {
        // Handle error (if needed)
        console.error('Error retrieving data:', error);
      });
  }, []); // Empty dependency array, runs once on mount

  const handleSubmit = (event) => {
    event.preventDefault();

    // Get the values from the form
    const carName = event.currentTarget.carName.value;
    const carPlate = event.currentTarget.carPlate.value;

    // Create the data object to be sent to the API
    const data = {
      registrationPlate: carPlate,
      nameCar: carName,
      isDeleted: false,
    };

    // Make the POST request using Axios
    axios.post('http://sbapi.ddns.net:8082/api/cars', data)
      .then((response) => {
        // Handle success (if needed)
        Swal.fire({
          title: "Success",
          text: "Data sent successfully",
          icon: "success",
        });
        console.log('Data sent successfully:', response.data);
      })
      .catch((error) => {
        // Handle error (if needed)
        Swal.fire({
          title: "Error",
          text: "Error sending data",
          icon: "error",
        });
        console.error('Error sending data:', error);
      });
  };

  const columns = [
    { field: 'id', headerName: 'ID', width: 70 },
    { field: 'nameCar', headerName: 'Car Name/Model', width: 200 },
    { field: 'registrationPlate', headerName: 'Registration Plate', width: 200 },
    { field: 'isDeleted', headerName: 'Is Deleted', width: 120 },
  ];

  // Hook to check if user prefers dark mode
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

  return (
    <ThemeProvider theme={theme}>
      {/* Rendering the NavBar component */}
      <ResponsiveAppBar />

      <br />

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
          <Typography component="h1" variant="h5">
            Register New Car
          </Typography>
          <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 3 }}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  name="carName"
                  required
                  fullWidth
                  id="carName"
                  label="Car Name/Model"
                  variant="standard"
                  autoFocus
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  required
                  fullWidth
                  name="carPlate"
                  id="carPlate"
                  label="Car Registration Plate"
                  variant="standard"
                />
              </Grid>
            </Grid>
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              Register
            </Button>
          </Box>
        </Box>
      </Container>
      <Container component="main">
        <div style={{ height: 400, width: '100%' }}>
          <DataGrid
            rows={cars}
            columns={columns}
            pageSize={5}
            checkboxSelection
          />
        </div>
      </Container>
    </ThemeProvider>
  );
}

export default Cars;
