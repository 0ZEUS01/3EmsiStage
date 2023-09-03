import React, { useState, useEffect } from 'react';
import {
  List,
  ListItem,
  ListItemText,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Pagination,
} from '@mui/material';
import Axios from 'axios';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import CssBaseline from '@mui/material/CssBaseline';
import Container from '@mui/material/Container';
import ResponsiveAppBar from './navBar';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';

const CarList = () => {
  // Getting the api url
  const apiUrl = process.env.REACT_APP_API_IP;

  const [openSuccessSnackbar, setOpenSuccessSnackbar] = useState(false);
  const [openErrorSnackbar, setOpenErrorSnackbar] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [cars, setCars] = useState([]);
  const [selectedCar, setSelectedCar] = useState(null);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [openDeleteConfirmation, setOpenDeleteConfirmation] = useState(false);
  const [openCreateDialog, setOpenCreateDialog] = useState(false);
  const [newCarData, setNewCarData] = useState({
    nameCar: '',
    registrationPlate: '',
    isDeleted: false,
  });
  const [editedCar, setEditedCar] = useState({
    registrationPlate: '',
    nameCar: '',
  });
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6; // Number of items per page

  const handlePageChange = (event, page) => {
    setCurrentPage(page);
  };

  const handleEdit = (car) => {
    setSelectedCar(car);
    setEditedCar({
      registrationPlate: car.registrationPlate,
      nameCar: car.nameCar,
    });
    setOpenEditDialog(true);
  };

  const handleCreateDialogOpen = () => {
    setOpenCreateDialog(true);
  };

  const handleCreateDialogClose = () => {
    setOpenCreateDialog(false);
    setNewCarData({
      nameCar: '',
      registrationPlate: '',
    });
  };

  const handleCreateDialogSubmit = () => {
    // Make POST request to create a new car
    Axios.post(apiUrl + '/api/cars', newCarData)
      .then(response => {
        // Refresh the cars list with the new car
        setCars(prevCars => [...prevCars, response.data]);
        setOpenCreateDialog(false);
        // Clear the input fields
        setNewCarData({
          nameCar: '',
          registrationPlate: '',
          isDeleted: false,
        });
        handleSuccessSnackbarOpen('Car created successfully.');
        // Add a delay (e.g., 2 seconds) before refresh
        setTimeout(() => {
          window.location.reload();
        }, 2000); // 2000 milliseconds = 2 seconds
      })
      .catch(error => {
        console.error(error);
        handleErrorSnackbarOpen('Error creating car. Please try again.');
      });
  };

  const handleEditChange = (event) => {
    const { name, value } = event.target;
    setEditedCar(prevCar => ({
      ...prevCar,
      [name]: value,
    }));
  };

  const handleEditSubmit = () => {
    // Make PUT request to update car
    Axios.put(apiUrl + `/api/cars/${selectedCar.id}`, editedCar)
      .then(response => {
        // Update the cars list with the updated car
        setCars(prevCars => prevCars.map(car => car.id === selectedCar.id ? response.data : car));
        setOpenEditDialog(false);
        handleSuccessSnackbarOpen('Car edited successfully.');
        // Add a delay (e.g., 2 seconds) before refresh
        setTimeout(() => {
          window.location.reload();
        }, 2000); // 2000 milliseconds = 2 seconds
      })
      .catch(error => {
        console.error(error);
        handleErrorSnackbarOpen('Error editing car. Please try again.');
      });
  };

  const handleDeleteConfirm = () => {
    // Make DELETE request to delete car
    Axios.delete(apiUrl + `/api/cars/${selectedCar.id}`)
      .then(() => {
        // Remove the deleted car from the cars list
        setCars(prevCars => prevCars.filter(car => car.id !== selectedCar.id));
        setOpenDeleteConfirmation(false);
        handleSuccessSnackbarOpen('Car deleted successfully.');
        // Add a delay (e.g., 2 seconds) before refresh
        setTimeout(() => {
          window.location.reload();
        }, 2000); // 2000 milliseconds = 2 seconds
      })
      .catch(error => {
        console.error(error);
        handleErrorSnackbarOpen('Error deleting car. Please try again.');
      });
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

  useEffect(() => {
    // Fetch cars data from the API
    Axios.get(apiUrl + '/api/cars')
      .then(response => setCars(response.data))
      .catch(error => console.error(error));
  }, [apiUrl]);

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
            <h1>List of cars</h1>
            <Button variant="contained" color="primary" onClick={handleCreateDialogOpen}>
              Create A Car
            </Button>
          </div>
          <List>
            {/* List of cars */}
            {cars
              .filter(car => !car.isDeleted)
              .slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage)
              .map(car => (
                <div key={car.id}>
                  <ListItem sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
                    <ListItemText primary={car.nameCar} secondary={car.registrationPlate} />
                    <div>
                      <Button variant="outlined" onClick={() => handleEdit(car)}>
                        Edit
                      </Button>
                      <Button
                        variant="outlined"
                        onClick={() => {
                          setSelectedCar(car); // Set the selected car before opening delete dialog
                          setOpenDeleteConfirmation(true);
                        }}
                      >
                        Delete
                      </Button>
                    </div>
                  </ListItem>
                  <hr style={{ margin: '5px 0' }} />
                </div>
              ))}
          </List>
          {/* Pagination */}
          <Pagination
            count={Math.ceil(cars.filter(car => !car.isDeleted).length / itemsPerPage)}
            page={currentPage}
            onChange={handlePageChange}
            color="primary"
            style={{ marginTop: '20px', display: 'flex', justifyContent: 'center' }}
          />
          {/* Create Car Dialog */}
          <Dialog open={openCreateDialog} onClose={handleCreateDialogClose}>
            <DialogTitle>Create A Car</DialogTitle>
            <DialogContent>
              <TextField
                label="Car Name/Model"
                name="nameCar"
                value={newCarData.nameCar}
                onChange={event =>
                  setNewCarData({ ...newCarData, nameCar: event.target.value })
                }
                fullWidth
                variant="standard"
                sx={{ marginBottom: 2 }}
              />
              <TextField
                label="Car Registration Plate"
                name="registrationPlate"
                value={newCarData.registrationPlate}
                onChange={event =>
                  setNewCarData({ ...newCarData, registrationPlate: event.target.value })
                }
                fullWidth
                variant="standard"
              />
            </DialogContent>
            <DialogActions>
              <Button onClick={handleCreateDialogClose}>Cancel</Button>
              <Button onClick={handleCreateDialogSubmit} color="primary">
                Create
              </Button>
            </DialogActions>
          </Dialog>
          {/* Edit Dialog */}
          <Dialog open={openEditDialog} onClose={() => setOpenEditDialog(false)}>
            <DialogTitle>Edit Car</DialogTitle>
            <DialogContent>
              <TextField
                label="Name"
                name="nameCar"
                value={editedCar.nameCar}
                onChange={handleEditChange}
                fullWidth
                variant="standard"
                sx={{ marginBottom: 2 }}
              />
              <TextField
                label="Registration Plate"
                name="registrationPlate"
                value={editedCar.registrationPlate}
                onChange={handleEditChange}
                variant="standard"
                fullWidth
              />
              {/* Add more fields for other properties */}
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenEditDialog(false)}>Cancel</Button>
              <Button onClick={handleEditSubmit}>Save</Button>
            </DialogActions>
          </Dialog>
          {/* Delete Confirmation */}
          <Dialog open={openDeleteConfirmation} onClose={() => setOpenDeleteConfirmation(false)}>
            <DialogTitle>Confirm Deletion</DialogTitle>
            <DialogContent>
              {selectedCar ? (
                <div>
                  Are you sure you want to delete {selectedCar.nameCar}?
                </div>
              ) : (
                <div>No car selected for deletion.</div>
              )}
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenDeleteConfirmation(false)}>Cancel</Button>
              {selectedCar && (
                <Button onClick={handleDeleteConfirm} color="error">
                  Delete
                </Button>
              )}
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

export default CarList;
