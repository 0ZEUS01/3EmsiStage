import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import CssBaseline from '@mui/material/CssBaseline';
import { useNavigate } from 'react-router-dom';

import 'leaflet/dist/leaflet.css';

import ResponsiveAppBar from './navBar';


const Locations = () => {
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



  const [plate, setRegistrationPlate] = React.useState('');
  const [selectedCar, setSelectedCar] = useState(null);
  const [locationsData, setLocationsData] = useState([]);

  const handleChange = (event) => {
    const selectedPlate = event.target.value;
    setRegistrationPlate(selectedPlate);

    if (selectedPlate === 'Show All' || selectedPlate === '') {
      setSelectedCar(null);
    } else {
      axios
        .get(`http://sbapi.ddns.net:8082/api/locations/${selectedPlate}`)
        .then((response) => {
          setSelectedCar(response.data);
          setLocationsData([response.data]); // Update locationsData with the fetched data
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

    // Set up interval to fetch new data every 1 minute (adjust the interval as needed)
    const interval = setInterval(() => {
      fetchLocationsData();
    }, 1000); // 1 minute (in milliseconds)

    // Clean up the interval on unmount to avoid memory leaks
    return () => {
      clearInterval(interval);
    };
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
            const isSelectedCar =
              selectedCar && location.car.registrationPlate.toUpperCase() === selectedCar.car.registrationPlate.toUpperCase();
            const showMarker = plate ? isSelectedCar : true; // Determine whether to show the marker based on plate selection
            return (
              showMarker && (
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
                  opacity={isSelectedCar ? 1 : 0.5}
                >
                  <Popup>
                    Registration Plate : {location.car.registrationPlate} <br /> Longitude : {location.longitude} Latitude : {location.latitude}
                  </Popup>
                </Marker>
              )
            );
          })}
        </MapContainer>
      </div>
    </ThemeProvider>
  );
};

export default Locations;
