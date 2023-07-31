import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import CssBaseline from '@mui/material/CssBaseline';

import 'leaflet/dist/leaflet.css';

import ResponsiveAppBar from './navBar';

const Locations = () => {
  const [plate, setRegistrationPlate] = React.useState('');
  const [selectedCar, setSelectedCar] = useState(null);

  const handleChange = (event) => {
    const selectedPlate = event.target.value;
    setRegistrationPlate(selectedPlate);

    if (selectedPlate === 'Show All' || selectedPlate === '') {
      setSelectedCar(null);
      axios
        .get('http://sbapi.ddns.net:8082/api/locations')
        .then((response) => {
          setLocationsData(response.data);
        })
        .catch((error) => {
          console.error('Error fetching locations:', error);
        });
    } else {
      // Fetch location data for the selected registration plate
      axios
        .get(`http://sbapi.ddns.net:8082/api/locations/${selectedPlate}`)
        .then((response) => {
          setSelectedCar(response.data);
        })
        .catch((error) => {
          console.error('Error fetching selected car details:', error);
        });
    }
  };

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

  // State variable to store the fetched locations data
  const [locationsData, setLocationsData] = useState([]);

  // Function to fetch all locations data from the API
  const fetchLocationsData = () => {
    axios
      .get('http://sbapi.ddns.net:8082/api/locations')
      .then((response) => {
        setLocationsData(response.data);
      })
      .catch((error) => {
        console.error('Error fetching locations:', error);
      });
  };

  // Fetch all locations data from the API when the component mounts
  useEffect(() => {
    fetchLocationsData();
  }, []);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {/* Rendering the NavBar component */}
      <ResponsiveAppBar />
      <br />

      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        Select Registration Plate To Track :
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={plate}
          label="registrationPlate"
          onChange={handleChange}
        >
          <MenuItem value="">Show All</MenuItem>
          {Array.from(new Set(locationsData.map((location) => location.car.registrationPlate.toUpperCase()))).map(
            (uniquePlate) => (
              <MenuItem key={uniquePlate} value={uniquePlate}>
                {uniquePlate}
              </MenuItem>
            ),
          )}
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

          {/* Add Markers for each location */}
          {locationsData.map((location) => {
            const isSelectedCar = selectedCar && location.car.registrationPlate.toUpperCase() === selectedCar.car.registrationPlate.toUpperCase();
            return (
              <Marker
                key={location.id}
                position={[location.latitude, location.longitude]}
                icon={
                  L.icon({
                    iconUrl:
                      'https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Simpleicons_Places_map-marker-point.svg/2048px-Simpleicons_Places_map-marker-point.svg.png',
                    iconSize: [20, 20],
                    iconAnchor: [10, 10], // Adjust this as needed
                    popupAnchor: [0, -10], // Adjust this as needed
                  })
                }
                opacity={plate ? (isSelectedCar ? 1 : 0) : 1}
              >
                <Popup>
                  Registration Plate : {location.car.registrationPlate} <br /> Longitude : {location.longitude}{' '}
                  Latitude : {location.latitude}
                </Popup>
              </Marker>
            );
          })}
        </MapContainer>
      </div>
    </ThemeProvider>
  );
};

export default Locations;