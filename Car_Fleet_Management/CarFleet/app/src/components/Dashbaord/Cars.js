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

const CarList = () => {
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
    Axios.post('http://sbapi.ddns.net:8082/api/cars', newCarData)
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
      })
      .catch(error => console.error(error));
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
    Axios.put(`http://sbapi.ddns.net:8082/api/cars/${selectedCar.id}`, editedCar)
      .then(response => {
        // Update the cars list with the updated car
        setCars(prevCars => prevCars.map(car => car.id === selectedCar.id ? response.data : car));
        setOpenEditDialog(false);
      })
      .catch(error => console.error(error));
  };

  const handleDeleteConfirm = () => {
    // Make DELETE request to delete car
    Axios.delete(`http://sbapi.ddns.net:8082/api/cars/${selectedCar.id}`)
      .then(() => {
        // Remove the deleted car from the cars list
        setCars(prevCars => prevCars.filter(car => car.id !== selectedCar.id));
        setOpenDeleteConfirmation(false);
      })
      .catch(error => console.error(error));
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
    Axios.get('http://sbapi.ddns.net:8082/api/cars')
      .then(response => setCars(response.data))
      .catch(error => console.error(error));
  }, []);

  return (
    <div>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <ResponsiveAppBar />
        <Container>
          <div style={{ textAlign: 'center', marginBottom: '20px' }}>
            <h1>List Of Cars</h1>
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
      </ThemeProvider>
    </div>
  );
};

export default CarList;
