package CarFleet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import CarFleet.Model.*;
import CarFleet.Service.CarFleetServiceEmail;
import CarFleet.Service.CarFleetServiceUser;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
public class CarFleetControllerUser {
	@Autowired(required=true)
    private CarFleetServiceUser carFleetServiceUser;
	
	@Autowired
    private CarFleetServiceEmail carFleetServiceEmail;

	                                     /* USER ENDPOINTS */
	
	// Endpoint to login user
	@PostMapping("/api/login")
	public User loginUser(@RequestBody Map<String, String> requestBody) throws SQLException {
	    return carFleetServiceUser.loginUser(requestBody.get("username_email"), requestBody.get("password"));
	}
	
    // Endpoint to get all users
    @GetMapping("/api/users")
    public List<User> getAllUsers() throws SQLException {
        return carFleetServiceUser.getAllUsers();
    }

    // Endpoint to get a specific user by ID
    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable Long id) throws SQLException {
        return carFleetServiceUser.getUserById(id);
    }

    // Endpoint to create a new user
    @PostMapping("/api/register")
    public User createUser(@RequestBody User user) throws SQLException {
        return carFleetServiceUser.createUser(user);
    }

    // Endpoint to update an existing user
    @PutMapping("/api/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) throws SQLException {
        return carFleetServiceUser.updateUser(id, updatedUser);
    }

    // Endpoint to delete a user
    @DeleteMapping("/api/users/{id}")
    public void deleteUser(@PathVariable Long id) throws SQLException {
    	carFleetServiceUser.deleteUser(id);
    }
    
    // Endpoint to recover user password
    @GetMapping("/api/users/{username_email}/recover")
    public int recoverUser(@PathVariable String username_email)throws SQLException{
    	String email_password = carFleetServiceUser.recoverUser(username_email);
    	String[] parts = email_password.split("\\|");
    	String email = parts[0];
        String newPassword = parts[1];
    	int result;
    	if(email.isEmpty() || email.isBlank()) {
    		result = -1;
    		return result;
    	}
    	else {
    		// Send a password recovery email
            String subject = "Password Recovery";
            String message = "Your new password is: " + newPassword;
            
            carFleetServiceEmail.sendEmail(email, subject, message);
            
            result = 1;
            return result;
    	}
    }
}
