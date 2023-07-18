package CarFleet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import CarFleet.Model.*;
import CarFleet.Service.CarFleetService;

import java.sql.SQLException;
import java.util.List;

@RestController
public class CarFleetControllerLocation {
	@Autowired
    private CarFleetService carFleetService;
	
                                       /* LOCATION ENDPOINTS */
    
    // Endpoint to get all locations
    @GetMapping("/api/locations")
    public List<Location> getAllLocations() throws SQLException {
        return carFleetService.getAllLocations();
    }

    // Endpoint to get a specific location by ID
    @GetMapping("/api/locations/{plate}")
    public Location getLocationById(@PathVariable String plate) throws SQLException {
        return carFleetService.getLocationByRegistrationPlate(plate);
    }

    // Endpoint to create a new location
    @PostMapping("/api/locations")
    public Location createLocation(@RequestBody Location location) throws SQLException {
        return carFleetService.createLocation(location);
    }

    // Endpoint to update an existing location
    @PutMapping("/api/locations/{plate}")
    public Location updateLocation(@PathVariable String plate, @RequestBody Location updatedLocation) throws SQLException {
        return carFleetService.updateLocation(plate, updatedLocation);
    }

    // Endpoint to delete a location
    @DeleteMapping("/api/locations/{plate}")
    public void deleteLocation(@PathVariable String plate) throws SQLException {
        carFleetService.deleteLocation(plate);
    }
}
