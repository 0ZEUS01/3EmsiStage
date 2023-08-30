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
} from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import CssBaseline from '@mui/material/CssBaseline';
import Container from '@mui/material/Container';
import ResponsiveAppBar from './navBar';

import './Users.css'; // Import your custom CSS file

const Users = () => {
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
      const response = await axios.get('http://sbapi.ddns.net:8082/api/users');
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const fetchNationalities = async () => {
    try {
      const response = await axios.get('http://sbapi.ddns.net:8082/api/nationality');
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

        await axios.put(`http://sbapi.ddns.net:8082/api/users/${editedUser.id}`, requestData);
        setOpenEditDialog(false);
        fetchData();
      } else {
        console.error('Selected nationality not found.');
      }
    } catch (error) {
      console.error('Error editing user:', error);
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

        await axios.post('http://sbapi.ddns.net:8082/api/register', requestData);
        setOpenCreateDialog(false);
        fetchData();
      } else {
        console.error('Selected nationality not found.');
      }
    } catch (error) {
      console.error('Error creating user:', error);
    }
  };

  const handleDeleteUser = async (userId) => {
    try {
      await axios.delete(`http://sbapi.ddns.net:8082/api/users/${userId}`);
      setOpenDeleteDialog(false);
      setUserToDelete(null);
      fetchData();
    } catch (error) {
      console.error('Error deleting user:', error);
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

  return (
    <div>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <ResponsiveAppBar />
        <Container>
          <div style={{ textAlign: 'center', marginBottom: '20px' }}>
            <h1>List Of Users</h1>
            <Button variant="contained" onClick={handleCreateClick}>Create User</Button>

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
              <div className="dialog-content">
                <TextField label="First Name" value={editedUser.fname || ''} onChange={e => setEditedUser({ ...editedUser, fname: e.target.value })} />
                <TextField label="Last Name" value={editedUser.lname || ''} onChange={e => setEditedUser({ ...editedUser, lname: e.target.value })} />
                <TextField label="Birthdate" type="date" value={editedUser.birthdate || ''} onChange={e => setEditedUser({ ...editedUser, birthdate: e.target.value })} />
                <TextField label="Username" value={editedUser.username || ''} onChange={e => setEditedUser({ ...editedUser, username: e.target.value })} />
                <TextField label="Email" value={editedUser.email || ''} onChange={e => setEditedUser({ ...editedUser, email: e.target.value })} />
                <TextField label="Password" type="password" value={'****'} onChange={e => setEditedUser({ ...editedUser, password: e.target.value })} />
                <TextField
                  label="Nationality"
                  select
                  value={editedUser.nationality || ''}
                  onChange={e => setEditedUser({ ...editedUser, nationality: e.target.value })}
                >
                  {nationalities.map(nationality => (
                    <MenuItem key={nationality.id} value={nationality.nationality}>
                      {nationality.nationality}
                    </MenuItem>
                  ))}
                </TextField>
              </div>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenEditDialog(false)}>Cancel</Button>
              <Button onClick={() => handleEditUser(editedUser)}>Save</Button>
            </DialogActions>
          </Dialog>

          <Dialog open={openCreateDialog} onClose={() => setOpenCreateDialog(false)}>
            <DialogTitle>Create User</DialogTitle>
            <DialogContent>
              <div className="dialog-content">
                <TextField label="First Name" value={newUser.fname} onChange={e => setNewUser({ ...newUser, fname: e.target.value })} />
                <TextField label="Last Name" value={newUser.lname} onChange={e => setNewUser({ ...newUser, lname: e.target.value })} />
                <TextField label="Birthdate" type="date" value={newUser.birthdate} onChange={e => setNewUser({ ...newUser, birthdate: e.target.value })} />
                <TextField label="Username" value={newUser.username} onChange={e => setNewUser({ ...newUser, username: e.target.value })} />
                <TextField label="Email" value={newUser.email} onChange={e => setNewUser({ ...newUser, email: e.target.value })} />
                <TextField label="Password" type="password" value={newUser.password} onChange={e => setNewUser({ ...newUser, password: e.target.value })} />
                <TextField
                  label="Nationality"
                  select
                  value={newUser.nationality}
                  onChange={e => setNewUser({ ...newUser, nationality: e.target.value })}
                >
                  {nationalities.map(nationality => (
                    <MenuItem key={nationality.id} value={nationality.id}>
                      {nationality.nationality}
                    </MenuItem>
                  ))}
                </TextField>
              </div>
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

      </ThemeProvider>

    </div>
  );
};

export default Users;
