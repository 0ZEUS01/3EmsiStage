import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  List,
  ListItem,
  ListItemText,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  TextField,
  DialogActions,
  MenuItem,
  Pagination,
  Grid,
} from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import CssBaseline from '@mui/material/CssBaseline';
import Container from '@mui/material/Container';
import ResponsiveAppBar from './navBar';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';

import './Users.css'; // Import your custom CSS file

const Users = () => {
  // Getting the api url
  const apiUrl = process.env.REACT_APP_API_IP;

  const [openSuccessSnackbar, setOpenSuccessSnackbar] = useState(false);
  const [openErrorSnackbar, setOpenErrorSnackbar] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [users, setUsers] = useState([]);
  const [nationalities, setNationalities] = useState([]);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [editedUser, setEditedUser] = useState({});
  const [openCreateDialog, setOpenCreateDialog] = useState(false);
  const [newUser, setNewUser] = useState({
    fname: '',
    lname: '',
    birthdate: '',
    username: '',
    email: '',
    password: '',
    nationality: ''
  });
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [userToDelete, setUserToDelete] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6; // Number of items per page

  useEffect(() => {
    fetchData();
    fetchNationalities();
  }, []);

  const fetchData = async () => {
    try {
      const response = await axios.get(apiUrl + '/api/users');
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const fetchNationalities = async () => {
    try {
      const response = await axios.get(apiUrl + '/api/nationality');
      setNationalities(response.data);
    } catch (error) {
      console.error('Error fetching nationalities:', error);
    }
  };

  const handleEditClick = (user) => {
    setEditedUser({
      ...user,
      nationality: user.nationality.nationality
    });
    setOpenEditDialog(true);
  };

  const handleCreateClick = () => {
    setOpenCreateDialog(true);
  };

  const handleDeleteClick = (user) => {
    setUserToDelete(user);
    setOpenDeleteDialog(true);
  };

  const handleEditUser = async (editedUser) => {
    try {
      const selectedNationality = nationalities.find(n => n.nationality === editedUser.nationality);

      if (selectedNationality) {
        const requestData = {
          fname: editedUser.fname,
          lname: editedUser.lname,
          birthdate: editedUser.birthdate,
          username: editedUser.username,
          email: editedUser.email,
          password: editedUser.password,
          nationality: {
            id: selectedNationality.id,
            nationality: selectedNationality.nationality
          },
          isDeleted: false
        };

        await axios.put(apiUrl + `/api/users/${editedUser.id}`, requestData);
        setOpenEditDialog(false);
        fetchData();
        handleSuccessSnackbarOpen('User edited successfully.');
        // Add a delay (e.g., 2 seconds) before refresh
        setTimeout(() => {
          window.location.reload();
        }, 2000); // 2000 milliseconds = 2 seconds
      } else {
        console.error('Selected nationality not found.');
        handleErrorSnackbarOpen('Error editing user. Please try again.');
      }
    } catch (error) {
      console.error('Error editing user:', error);
      handleErrorSnackbarOpen('Error editing user. Please try again.');
    }
  };

  const handleCreateUser = async (newUser) => {
    try {
      const selectedNationality = nationalities.find(n => n.id === newUser.nationality);

      if (selectedNationality) {
        const requestData = {
          ...newUser,
          nationality: {
            id: selectedNationality.id,
            nationality: selectedNationality.nationality
          },
          isDeleted: false
        };

        await axios.post(apiUrl + '/api/register', requestData);
        setOpenCreateDialog(false);
        fetchData();
        handleSuccessSnackbarOpen('User created successfully.');
        // Add a delay (e.g., 2 seconds) before refresh
        setTimeout(() => {
          window.location.reload();
        }, 2000); // 2000 milliseconds = 2 seconds
      } else {
        console.error('Selected nationality not found.');
        handleErrorSnackbarOpen('Error creating user. Please try again.');
      }
    } catch (error) {
      console.error('Error creating user:', error);
      handleErrorSnackbarOpen('Error creating user. Please try again.');
    }
  };

  const handleDeleteUser = async (userId) => {
    try {
      await axios.delete(apiUrl + `/api/users/${userId}`);
      setOpenDeleteDialog(false);
      setUserToDelete(null);
      fetchData();
      handleSuccessSnackbarOpen('User deleted successfully.');
      // Add a delay (e.g., 2 seconds) before refresh
      setTimeout(() => {
        window.location.reload();
      }, 2000); // 2000 milliseconds = 2 seconds
    } catch (error) {
      console.error('Error deleting user:', error);
      handleErrorSnackbarOpen('Error deleting user. Please try again.');
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
    [prefersDarkMode]
  );

  const indexOfLastUser = currentPage * itemsPerPage;
  const indexOfFirstUser = indexOfLastUser - itemsPerPage;
  const displayedUsers = users.filter(user => !user.isDeleted).slice(indexOfFirstUser, indexOfLastUser);

  const handleSuccessSnackbarOpen = (message) => {
    setSnackbarMessage(message);
    setOpenSuccessSnackbar(true);
  };

  const handleErrorSnackbarOpen = (message) => {
    setSnackbarMessage(message);
    setOpenErrorSnackbar(true);
  };

  const handleSnackbarClose = () => {
    setOpenSuccessSnackbar(false);
    setOpenErrorSnackbar(false);
  };


  return (
    <div>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <ResponsiveAppBar />
        <Container>
          <div style={{ textAlign: 'center', marginBottom: '20px' }}>
            <h1>List of users</h1>
            <Button variant="contained" onClick={handleCreateClick}>Create A User</Button>

          </div>
          <List>
            {displayedUsers.map((user, index) => (
              <React.Fragment key={user.id}>
                <ListItem>
                  <ListItemText
                    primary={`${user.fname} ${user.lname}`}
                    secondary={`Username: ${user.username}, Email: ${user.email}, Birthdate: ${user.birthdate}, Nationality: ${user.nationality.nationality}`}
                  />
                  <Button variant="outlined" onClick={() => handleEditClick(user)}>Edit</Button>
                  <Button variant="outlined" onClick={() => handleDeleteClick(user)}>Delete</Button>
                </ListItem>
                {index !== displayedUsers.length - 1 && <hr />} {/* Add <hr> except for the last item */}
              </React.Fragment>
            ))}
          </List>
          <Pagination
            count={Math.ceil(users.filter(user => !user.isDeleted).length / itemsPerPage)}
            page={currentPage}
            onChange={(event, page) => setCurrentPage(page)}
            color="primary"
            style={{ marginTop: '20px', display: 'flex', justifyContent: 'center' }}
          />

          <Dialog open={openEditDialog} onClose={() => setOpenEditDialog(false)}>
            <DialogTitle>Edit User</DialogTitle>
            <DialogContent>
              <form className="dialog-content" onSubmit={(e) => e.preventDefault()}>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <TextField
                      label="First Name"
                      variant="standard"
                      fullWidth
                      value={editedUser.fname || ''}
                      onChange={(e) => setEditedUser({ ...editedUser, fname: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Last Name"
                      variant="standard"
                      fullWidth
                      value={editedUser.lname || ''}
                      onChange={(e) => setEditedUser({ ...editedUser, lname: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Birthdate"
                      variant="standard"
                      fullWidth
                      type="date"
                      value={editedUser.birthdate || ''}
                      onChange={(e) => setEditedUser({ ...editedUser, birthdate: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Username"
                      variant="standard"
                      fullWidth
                      value={editedUser.username || ''}
                      onChange={(e) => setEditedUser({ ...editedUser, username: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Email"
                      variant="standard"
                      fullWidth
                      value={editedUser.email || ''}
                      onChange={(e) => setEditedUser({ ...editedUser, email: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Password"
                      variant="standard"
                      fullWidth
                      type="password"
                      value={editedUser.password || ''}
                      onChange={(e) => setEditedUser({ ...editedUser, password: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={20}>
                    <TextField
                      label="Nationality"
                      variant="standard"
                      fullWidth
                      select
                      value={editedUser.nationality || ''}
                      onChange={(e) => setEditedUser({ ...editedUser, nationality: e.target.value })}
                    >
                      {nationalities.map((nationality) => (
                        <MenuItem key={nationality.id} value={nationality.nationality}>
                          {nationality.nationality}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                </Grid>
              </form>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenEditDialog(false)}>Cancel</Button>
              <Button onClick={() => handleEditUser(editedUser)}>Save</Button>
            </DialogActions>
          </Dialog>


          <Dialog open={openCreateDialog} onClose={() => setOpenCreateDialog(false)}>
            <DialogTitle>Create A User</DialogTitle>
            <DialogContent>
              <form className="dialog-content" onSubmit={(e) => e.preventDefault()}>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <TextField
                      label="First Name"
                      variant="standard"
                      fullWidth
                      value={newUser.fname}
                      onChange={(e) => setNewUser({ ...newUser, fname: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Last Name"
                      variant="standard"
                      fullWidth
                      value={newUser.lname}
                      onChange={(e) => setNewUser({ ...newUser, lname: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Birthdate"
                      variant="standard"
                      fullWidth
                      type="date"
                      value={newUser.birthdate}
                      onChange={(e) => setNewUser({ ...newUser, birthdate: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Username"
                      variant="standard"
                      fullWidth
                      value={newUser.username}
                      onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Email"
                      variant="standard"
                      fullWidth
                      value={newUser.email}
                      onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <TextField
                      label="Password"
                      variant="standard"
                      fullWidth
                      type="password"
                      value={newUser.password}
                      onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
                    />
                  </Grid>
                  <Grid item xs={20}>
                    <TextField
                      label="Nationality"
                      variant="standard"
                      fullWidth
                      select
                      value={newUser.nationality}
                      onChange={(e) => setNewUser({ ...newUser, nationality: e.target.value })}
                    >
                      {nationalities.map((nationality) => (
                        <MenuItem key={nationality.id} value={nationality.id}>
                          {nationality.nationality}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                </Grid>
              </form>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenCreateDialog(false)}>Cancel</Button>
              <Button onClick={() => handleCreateUser(newUser)}>Create</Button>
            </DialogActions>
          </Dialog>

          <Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)}>
            <DialogTitle>Delete User</DialogTitle>
            <DialogContent>
              Are you sure you want to delete {userToDelete && `${userToDelete.fname} ${userToDelete.lname}`}?
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenDeleteDialog(false)}>Cancel</Button>
              <Button onClick={() => handleDeleteUser(userToDelete.id)}>Delete</Button>
            </DialogActions>
          </Dialog>
        </Container>
        <Snackbar
          open={openSuccessSnackbar}
          autoHideDuration={5000}
          onClose={handleSnackbarClose}
          anchorOrigin={{
            vertical: 'top',
            horizontal: 'center',
          }}
        >
          <Alert onClose={handleSnackbarClose} severity="success">
            {snackbarMessage}
          </Alert>
        </Snackbar>
        <Snackbar
          open={openErrorSnackbar}
          autoHideDuration={5000}
          onClose={handleSnackbarClose}
          anchorOrigin={{
            vertical: 'top',
            horizontal: 'center',
          }}
        >
          <Alert onClose={handleSnackbarClose} severity="error">
            {snackbarMessage}
          </Alert>
        </Snackbar>

      </ThemeProvider>

    </div>
  );
};

export default Users;
