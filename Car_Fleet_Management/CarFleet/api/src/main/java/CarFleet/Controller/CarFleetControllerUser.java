package CarFleet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import CarFleet.Model.*;
import CarFleet.Service.CarFleetService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
public class CarFleetControllerUser {
	@Autowired
    private CarFleetService carFleetService;

	                                     /* USER ENDPOINTS */
	
	// Endpoint to login user
	@PostMapping("/api/login")
	public User loginUser(@RequestBody Map<String, String> requestBody) throws SQLException {
	    return carFleetService.loginUser(requestBody.get("username_email"), requestBody.get("password"));
	}
	
    // Endpoint to get all users
    @GetMapping("/api/users")
    public List<User> getAllUsers() throws SQLException {
        return carFleetService.getAllUsers();
    }

    // Endpoint to get a specific user by ID
    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable Long id) throws SQLException {
        return carFleetService.getUserById(id);
    }

    // Endpoint to create a new user
    @PostMapping("/api/register")
    public User createUser(@RequestBody User user) throws SQLException {
        return carFleetService.createUser(user);
    }

    // Endpoint to update an existing user
    @PutMapping("/api/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) throws SQLException {
        return carFleetService.updateUser(id, updatedUser);
    }

    // Endpoint to delete a user
    @DeleteMapping("/api/users/{id}")
    public void deleteUser(@PathVariable Long id) throws SQLException {
        carFleetService.deleteUser(id);
    }
}
