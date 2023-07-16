package CarFleet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import CarFleet.Model.*;
import CarFleet.Service.CarFleetService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
public class CarFleetController {
	@Autowired
    private CarFleetService carFleetService;

	                                     /* USER ENDPOINTS */
	
	// Endpoint to login user
	@PostMapping("/api/login")
	public Users loginUser(@RequestBody Map<String, String> requestBody) throws SQLException {
	    return carFleetService.loginUser(requestBody.get("username_email"), requestBody.get("password"));
	}
	
    // Endpoint to get all users
    @GetMapping("/api/users")
    public List<Users> getAllUsers() throws SQLException {
        return carFleetService.getAllUsers();
    }

    // Endpoint to get a specific user by ID
    @GetMapping("/api/users/{id}")
    public Users getUserById(@PathVariable Long id) throws SQLException {
        return carFleetService.getUserById(id);
    }

    // Endpoint to create a new user
    @PostMapping("/api/register")
    public Users createUser(@RequestBody Users user) throws SQLException {
        return carFleetService.createUser(user);
    }

    // Endpoint to update an existing user
    @PutMapping("/api/users/{id}")
    public Users updateUser(@PathVariable Long id, @RequestBody Users updatedUser) throws SQLException {
        return carFleetService.updateUser(id, updatedUser);
    }

    // Endpoint to delete a user
    @DeleteMapping("/api/users/{id}")
    public void deleteUser(@PathVariable Long id) throws SQLException {
        carFleetService.deleteUser(id);
    }

                                         /* CAR ENDPOINTS */
    
    // Endpoint to get all cars
    @GetMapping("/api/cars")
    public List<Cars> getAllCars() throws SQLException {
        return carFleetService.getAllCars();
    }

    // Endpoint to get a specific car by ID
    @GetMapping("/api/cars/{id}")
    public Cars getCarById(@PathVariable Long id) throws SQLException {
        return carFleetService.getCarById(id);
    }

    // Endpoint to create a new car
    @PostMapping("/api/cars")
    public Cars createCar(@RequestBody Cars car) throws SQLException {
        return carFleetService.createCar(car);
    }

    // Endpoint to update an existing car
    @PutMapping("/api/cars/{id}")
    public Cars updateCar(@PathVariable Long id, @RequestBody Cars updatedCar) throws SQLException {
        return carFleetService.updateCar(id, updatedCar);
    }

    // Endpoint to delete a car
    @DeleteMapping("/api/cars/{id}")
    public void deleteCar(@PathVariable Long id) throws SQLException {
        carFleetService.deleteCar(id);
    }

                                        /* LOCATION ENDPOINTS */
    
    // Endpoint to get all locations
    @GetMapping("/api/locations")
    public List<Locations> getAllLocations() throws SQLException {
        return carFleetService.getAllLocations();
    }

    // Endpoint to get a specific location by ID
    @GetMapping("/api/locations/{plate}")
    public Locations getLocationById(@PathVariable String plate) throws SQLException {
        return carFleetService.getLocationByRegistrationPlate(plate);
    }

    // Endpoint to create a new location
    @PostMapping("/api/locations")
    public Locations createLocation(@RequestBody Locations location) throws SQLException {
        return carFleetService.createLocation(location);
    }

    // Endpoint to update an existing location
    @PutMapping("/api/locations/{plate}")
    public Locations updateLocation(@PathVariable String plate, @RequestBody Locations updatedLocation) throws SQLException {
        return carFleetService.updateLocation(plate, updatedLocation);
    }

    // Endpoint to delete a location
    @DeleteMapping("/api/locations/{plate}")
    public void deleteLocation(@PathVariable String plate) throws SQLException {
        carFleetService.deleteLocation(plate);
    }
}
