import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { MapContainer, TileLayer, Marker, Popup, Polyline } from 'react-leaflet';
import L from 'leaflet';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import CssBaseline from '@mui/material/CssBaseline';
import { useNavigate } from 'react-router-dom';

import 'leaflet/dist/leaflet.css';

import ResponsiveAppBar from './navBar';

const LocationsHistory = () => {
  // Hook to access navigation function
  const navigate = useNavigate();

  // Function to check if "userData" exists in local storage
  useEffect(() => {
    const userData = localStorage.getItem('userData');
    if (!userData) {
      // Redirect to the desired page if "userData" doesn't exist
      navigate('/CarFleet/Login');
    }
  }, [navigate]);


  const [cars, setCars] = useState([]); // State to store the list of cars' registration plates
  const [selectedPlate, setSelectedPlate] = useState(''); // State to store the selected car's registration plate
  const [locationHistory, setLocationHistory] = useState([]); // State to store the selected car's location history
  const [pathCoordinates, setPathCoordinates] = useState([]); // State to store the path coordinates for the selected car's location history

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

  // Fetch the list of cars' registration plates from the API
  useEffect(() => {
    axios.get('http://sbapi.ddns.net:8082/api/cars')
      .then(response => {
        setCars(response.data);
      })
      .catch(error => {
        console.error('Error fetching car data:', error);
      });
  }, []);

  // Handler function for selecting a car from the drop-down menu
  const handleCarSelect = (event) => {
    const selectedCarPlate = event.target.value;
    setSelectedPlate(selectedCarPlate);

    // Make an API call to get the location history of the selected car
    axios.get(`http://sbapi.ddns.net:8082/api/locations/${selectedCarPlate}/history`)
      .then(response => {
        const history = response.data;
        setLocationHistory(history);

        // Extract and store the coordinates from the location history
        const coordinates = history.map(location => [location.latitude, location.longitude]);
        setPathCoordinates(coordinates);
      })
      .catch(error => {
        console.error('Error fetching location history:', error);
        setLocationHistory([]);
        setPathCoordinates([]);
      });
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {/* Rendering the NavBar component */}
      <ResponsiveAppBar />
      <br />

      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        Select Registration Plate To Trace The Car :
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          label="registrationPlate"
          value={selectedPlate}
          onChange={handleCarSelect}
        >
          <MenuItem value="">Show All</MenuItem>
          {cars.map(car => (
            <MenuItem key={car.registrationPlate} value={car.registrationPlate}>
              {car.registrationPlate}
            </MenuItem>
          ))}
        </Select>
      </div>

      <br />

      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        {/* Render the map container */}
        <MapContainer center={[31.7782632, -9.7908949]} zoom={6} style={{ height: '600px', width: '100%' }}>
          {/* Add the TileLayer component to show the map */}
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          />
          {/* Display the blue line for the selected car's location history */}
          {pathCoordinates.length > 0 && (
            <Polyline positions={pathCoordinates} color="blue" />
          )}
          {/* Display markers for the selected car's location history */}
          {locationHistory.map(location => (
            <Marker key={location.id} position={[location.latitude, location.longitude]} icon={
              L.icon({
                iconUrl:
                  'https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Simpleicons_Places_map-marker-point.svg/2048px-Simpleicons_Places_map-marker-point.svg.png',
                iconSize: [20, 20],
                iconAnchor: [10, 10], // Adjust this as needed
                popupAnchor: [0, -10], // Adjust this as needed
              })
            }>
              <Popup>
                {`Date: ${location.date} Time: ${location.time}`}
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
    </ThemeProvider>
  );
};

export default LocationsHistory;
