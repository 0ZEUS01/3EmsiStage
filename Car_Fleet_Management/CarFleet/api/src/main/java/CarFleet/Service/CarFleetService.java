package CarFleet.Service;

import org.springframework.stereotype.Service;
import CarFleet.Model.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarFleetService {

    public List<Users> getAllUsers() {
        List<Users> users = new ArrayList<>();
        // Retrieve users from a database
        return users;
    }

    public Users getUserById(Long id) {
        Users user = null;
        // Retrieve a user by ID from a database
        return user;
    }

    public Users createUser(Users user) {
        // Persist the user in a database
        return user;
    }

    public Users updateUser(Long id, Users updatedUser) {
        Users existingUser = getUserById(id);
        if (existingUser != null) {
            existingUser.setFname(updatedUser.getFname());
            existingUser.setLname(updatedUser.getLname());
            // Persist the updated user in a database
            return existingUser;
        }
        return null;
    }

    public void deleteUser(Long id) {
        // Delete the user by ID from a database
    }

    public List<Cars> getAllCars() {
        List<Cars> cars = new ArrayList<>();
        // Retrieve cars from a database
        return cars;
    }

    public Cars getCarById(Long id) {
        Cars car = null;
        // Retrieve a car by ID from a database
        return car;
    }

    public Cars createCar(Cars car) {
        // Persist the car in a database
        return car;
    }

    public Cars updateCar(Long id, Cars updatedCar) {
        Cars existingCar = getCarById(id);
        if (existingCar != null) {
            existingCar.setRegistrationPlate(updatedCar.getRegistrationPlate());
            existingCar.setNameCar(updatedCar.getNameCar());
            // Persist the updated car in a database
            return existingCar;
        }
        return null;
    }

    public void deleteCar(Long id) {
        // Delete the car by ID from a database
    }

    public List<Locations> getAllLocations() {
        List<Locations> locations = new ArrayList<>();
        // Retrieve locations from a database
        return locations;
    }

    public Locations getLocationById(Long id) {
        Locations location = null;
        // Retrieve a location by ID from a database
        return location;
    }

    public Locations createLocation(Locations location) {
        // Persist the location in a database
        return location;
    }

    public Locations updateLocation(Long id, Locations updatedLocation) {
        Locations existingLocation = getLocationById(id);
        if (existingLocation != null) {
            existingLocation.setLatitude(updatedLocation.getLatitude());
            existingLocation.setLongitude(updatedLocation.getLongitude());
            // Persist the updated location in a database
            return existingLocation;
        }
        return null;
    }

    public void deleteLocation(Long id) {
        // Delete the location by ID from a database
    }

}
