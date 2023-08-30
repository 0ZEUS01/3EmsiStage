import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Avatar from '@mui/material/Avatar';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import MenuItem from '@mui/material/MenuItem';
import Button from '@mui/material/Button';
import ResponsiveAppBar from './navBar';

const Profile = () => {
  const navigate = useNavigate();

  const storedUserData = JSON.parse(localStorage.getItem('userData'));

  const [userData, setUserData] = useState(storedUserData);
  const [isEditing, setIsEditing] = useState(false);
  const [nationalities, setNationalities] = useState([]);

  const [formData, setFormData] = useState({
    fname: userData ? userData.fname : '',
    lname: userData ? userData.lname : '',
    username: userData ? userData.username : '',
    email: userData ? userData.email : '',
    password: userData ? userData.password : '',
    birthdate: userData ? userData.birthdate : '',
    nationality: userData && userData.nationality ? userData.nationality.id : '',
  });

  useEffect(() => {
    fetch('http://sbapi.ddns.net:8082/api/nationality')
      .then(response => response.json())
      .then(data => {
        setNationalities(data);
      })
      .catch(error => {
        console.error('Error fetching nationalities:', error);
      });

    if (!userData) {
      navigate('/CarFleet/Login');
    }
  }, [navigate, userData]);

  const handleProfileUpdate = async (updatedData) => {
    try {
      const response = await fetch(`http://sbapi.ddns.net:8082/api/users/${updatedData.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedData),
      });

      if (response.ok) {
        console.log('Profile updated successfully!');
        setUserData(updatedData);
        localStorage.setItem('userData', JSON.stringify(updatedData));
        setIsEditing(false);
      } else {
        console.error('Failed to update profile:', response.statusText);
      }
    } catch (error) {
      console.error('Error updating profile:', error);
    }
  };

  const handleEditButtonClick = () => {
    setIsEditing(true);
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const updatedData = {
      ...userData,
      fname: formData.fname,
      lname: formData.lname,
      username: formData.username,
      email: formData.email,
      password: formData.password,
      birthdate: formData.birthdate,
      nationality: {
        id: formData.nationality,
        nationality: nationalities.find(option => option.id === formData.nationality)?.nationality,
      },
    };

    handleProfileUpdate(updatedData);
    console.log('Profile updated:', updatedData);
  };




  const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
  const theme = React.useMemo(
    () =>
      createTheme({
        palette: {
          mode: prefersDarkMode ? 'dark' : 'light',
        },
      }),
    [prefersDarkMode]
  );
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <ResponsiveAppBar />
      <br />
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
          Your Profile
        </Typography>
        <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                autoComplete="given-name"
                name="fname"
                required
                fullWidth
                id="fname"
                label="First Name"
                autoFocus
                disabled={!isEditing} // Toggle disabled prop based on isEditing
                variant="standard"
                onChange={(e) => setFormData({ ...formData, fname: e.target.value })}
                defaultValue={userData ? userData.fname : ''}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="lname"
                label="Last Name"
                name="lname"
                autoComplete="family-name"
                disabled={!isEditing} // Toggle disabled prop based on isEditing
                variant="standard"
                onChange={(e) => setFormData({ ...formData, lname: e.target.value })}
                defaultValue={userData ? userData.lname : ''}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="email"
                label="Email Address"
                name="email"
                autoComplete="email"
                disabled={!isEditing} // Toggle disabled prop based on isEditing
                variant="standard"
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                defaultValue={userData ? userData.email : ''}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="username"
                label="Username"
                name="username"
                disabled={!isEditing} // Toggle disabled prop based on isEditing
                variant="standard"
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                defaultValue={userData ? userData.username : ''}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="new-password"
                disabled={!isEditing} // Toggle disabled prop based on isEditing
                variant="standard"
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                defaultValue={userData ? userData.password : ''}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                name="birthdate"
                type="date"
                id="birthdate"
                disabled={!isEditing} // Toggle disabled prop based on isEditing
                variant="standard"
                onChange={(e) => setFormData({ ...formData, birthdate: e.target.value })}
                defaultValue={userData ? userData.birthdate : ''}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                select
                name="nationality"
                label="Nationality"
                id="nationality"
                variant="standard"
                fullWidth
                disabled={!isEditing} // Toggle disabled prop based on isEditing
                onChange={(e) => setFormData({ ...formData, nationality: e.target.value })}
                value={formData.nationality}
              >
                {nationalities.map((option) => (
                  <MenuItem key={option.id} value={option.id}>
                    {option.nationality}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Button
              type="button"
              fullWidth
              variant="contained"
              sx={{ mt: 4 }}
              onClick={isEditing ? handleSubmit : handleEditButtonClick}
            >
              {isEditing ? 'Submit Modifications' : 'Enable Edit'}
            </Button>
          </Grid>
        </Box>
      </Box>
    </ThemeProvider>
  );
};

export default Profile;
