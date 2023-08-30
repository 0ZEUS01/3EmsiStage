package CarFleet.Controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import CarFleet.Model.Nationality;
import CarFleet.Service.CarFleetServiceNationality;


@RestController
public class CarFleetControllerNationality {
	@Autowired(required=true)
	private CarFleetServiceNationality carFleetServiceLocationNationality;
	
	                                         /* NATIONALITY ENDPOINTS */
	
	// Endpoint to get all locations
    @GetMapping("/api/nationality")
    public List<Nationality> getAllLocationsHistory() throws SQLException {
        return carFleetServiceLocationNationality.getAllNationality();
    }
}
