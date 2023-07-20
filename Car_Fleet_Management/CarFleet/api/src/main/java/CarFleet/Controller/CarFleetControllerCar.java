package CarFleet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import CarFleet.Model.*;
import CarFleet.Service.CarFleetServiceCar;

import java.sql.SQLException;
import java.util.List;

@RestController
public class CarFleetControllerCar {
	@Autowired(required=true)
    private CarFleetServiceCar carFleetServiceCar;
	
                                            /* CAR ENDPOINTS */
    
    // Endpoint to get all cars
    @GetMapping("/api/cars")
    public List<Car> getAllCars() throws SQLException {
        return carFleetServiceCar.getAllCars();
    }

    // Endpoint to get a specific car by ID
    @GetMapping("/api/cars/{id}")
    public Car getCarById(@PathVariable Long id) throws SQLException {
        return carFleetServiceCar.getCarById(id);
    }

    // Endpoint to create a new car
    @PostMapping("/api/cars")
    public Car createCar(@RequestBody Car car) throws SQLException {
        return carFleetServiceCar.createCar(car);
    }

    // Endpoint to update an existing car
    @PutMapping("/api/cars/{id}")
    public Car updateCar(@PathVariable Long id, @RequestBody Car updatedCar) throws SQLException {
        return carFleetServiceCar.updateCar(id, updatedCar);
    }

    // Endpoint to delete a car
    @DeleteMapping("/api/cars/{id}")
    public void deleteCar(@PathVariable Long id) throws SQLException {
    	carFleetServiceCar.deleteCar(id);
    }
}
