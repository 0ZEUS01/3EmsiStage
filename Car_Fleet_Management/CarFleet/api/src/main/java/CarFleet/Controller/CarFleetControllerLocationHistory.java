package CarFleet.Controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import CarFleet.Model.Location_History;
import CarFleet.Service.CarFleetServiceLocationHistory;

@RestController
public class CarFleetControllerLocationHistory {
	@Autowired(required=true)
    private CarFleetServiceLocationHistory carFleetServiceLocationHistory;
	
	                                         /* LOCATION HISTORY ENDPOINTS */
	
	// Endpoint to get all locations
    @GetMapping("/api/locations/history")
    public List<Location_History> getAllLocationsHistory() throws SQLException {
        return carFleetServiceLocationHistory.getAllLocationsHistory();
    }

    // Endpoint to get a specific location by ID
    @GetMapping("/api/locations/{plate}/history")
    public List<Location_History> getLocationHistoryById(@PathVariable String plate) throws SQLException {
        return carFleetServiceLocationHistory.getLocationHistoryByRegistrationPlate(plate);
    }
}
