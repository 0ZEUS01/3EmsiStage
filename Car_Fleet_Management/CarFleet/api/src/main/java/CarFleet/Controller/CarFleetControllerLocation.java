package CarFleet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import CarFleet.Model.*;
import CarFleet.Service.CarFleetServiceLocation;

import java.sql.SQLException;
import java.util.List;

@RestController
public class CarFleetControllerLocation {
	@Autowired(required=true)
    private CarFleetServiceLocation carFleetServiceLocation;
	
                                       /* LOCATION ENDPOINTS */
    
    // Endpoint to get all locations
    @GetMapping("/api/locations")
    public List<Location> getAllLocations() throws SQLException {
        return carFleetServiceLocation.getAllLocations();
    }

    // Endpoint to get a specific location by ID
    @GetMapping("/api/locations/{plate}")
    public Location getLocationById(@PathVariable String plate) throws SQLException {
        return carFleetServiceLocation.getLocationByRegistrationPlate(plate);
    }

    // Endpoint to create a new location
    @PostMapping("/api/locations")
    public Location createLocation(@RequestBody Location location) throws SQLException {
        return carFleetServiceLocation.createLocation(location);
    }

    // Endpoint to update an existing location
    @PutMapping("/api/locations/{plate}")
    public Location updateLocation(@PathVariable String plate, @RequestBody Location updatedLocation) throws SQLException {
        return carFleetServiceLocation.updateLocation(plate, updatedLocation);
    }

    // Endpoint to delete a location
    @DeleteMapping("/api/locations/{plate}")
    public void deleteLocation(@PathVariable String plate) throws SQLException {
    	carFleetServiceLocation.deleteLocation(plate);
    }
    
    // Endpoint to update the location of a specific car in real-time
    @PutMapping("/api/locations/{plate}/update")
    public void updateLocationRealTime(@PathVariable String plate, @RequestBody Location updatedLocation) throws SQLException {
        // Implement the logic to update the location in real-time
        carFleetServiceLocation.updateLocationRealTime(plate, updatedLocation);
    }
}
