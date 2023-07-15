package CarFleet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import CarFleet.Model.*;
import CarFleet.Service.CarFleetService;

import java.util.List;

@RestController
public class CarFleetController {
	@Autowired
    private CarFleetService carFleetService;

    // Endpoint to get all users
    @GetMapping("/users")
    public List<Users> getAllUsers() {
        return carFleetService.getAllUsers();
    }

    // Endpoint to get a specific user by ID
    @GetMapping("/users/{id}")
    public Users getUserById(@PathVariable Long id) {
        return carFleetService.getUserById(id);
    }

    // Endpoint to create a new user
    @PostMapping("/users")
    public Users createUser(@RequestBody Users user) {
        return carFleetService.createUser(user);
    }

    // Endpoint to update an existing user
    @PutMapping("/users/{id}")
    public Users updateUser(@PathVariable Long id, @RequestBody Users updatedUser) {
        return carFleetService.updateUser(id, updatedUser);
    }

    // Endpoint to delete a user
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        carFleetService.deleteUser(id);
    }

    // Endpoint to get all cars
    @GetMapping("/cars")
    public List<Cars> getAllCars() {
        return carFleetService.getAllCars();
    }

    // Endpoint to get a specific car by ID
    @GetMapping("/cars/{id}")
    public Cars getCarById(@PathVariable Long id) {
        return carFleetService.getCarById(id);
    }

    // Endpoint to create a new car
    @PostMapping("/cars")
    public Cars createCar(@RequestBody Cars car) {
        return carFleetService.createCar(car);
    }

    // Endpoint to update an existing car
    @PutMapping("/cars/{id}")
    public Cars updateCar(@PathVariable Long id, @RequestBody Cars updatedCar) {
        return carFleetService.updateCar(id, updatedCar);
    }

    // Endpoint to delete a car
    @DeleteMapping("/cars/{id}")
    public void deleteCar(@PathVariable Long id) {
        carFleetService.deleteCar(id);
    }

    // Endpoint to get all locations
    @GetMapping("/locations")
    public List<Locations> getAllLocations() {
        return carFleetService.getAllLocations();
    }

    // Endpoint to get a specific location by ID
    @GetMapping("/locations/{id}")
    public Locations getLocationById(@PathVariable Long id) {
        return carFleetService.getLocationById(id);
    }

    // Endpoint to create a new location
    @PostMapping("/locations")
    public Locations createLocation(@RequestBody Locations location) {
        return carFleetService.createLocation(location);
    }

    // Endpoint to update an existing location
    @PutMapping("/locations/{id}")
    public Locations updateLocation(@PathVariable Long id, @RequestBody Locations updatedLocation) {
        return carFleetService.updateLocation(id, updatedLocation);
    }

    // Endpoint to delete a location
    @DeleteMapping("/locations/{id}")
    public void deleteLocation(@PathVariable Long id) {
        carFleetService.deleteLocation(id);
    }
}
