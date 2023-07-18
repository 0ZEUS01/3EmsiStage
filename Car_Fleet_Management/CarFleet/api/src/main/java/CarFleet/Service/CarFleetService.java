package CarFleet.Service;

import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import CarFleet.Model.*;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarFleetService {
	
	                         /* CONSTRUCTOR INITIALIZE ConnectDB SERVICES */
	
	private ConnectDB connectDB;
	
	public CarFleetService() {
        connectDB = new ConnectDB();
    }
    
	                                      /* USERS SERVICES */
	
	public User loginUser(String username_email, String password) throws SQLException{
		User user = null;
		
        connectDB.connect(); // Establish the database connection

        java.sql.Connection connection = connectDB.getConnection();
        String sql = "SELECT U.id_user, U.fname_user, U.lname_user, U.birthdate_user, U.username_user, U.email_user, U.password_user, U.nationality_user, N.nationality, U.isDeleted FROM Users U JOIN nationalities N ON U.nationality_user = N.id_nationality WHERE (U.username_user = ? OR U.email_user = ?) AND isDeleted = 0";
        try (PreparedStatement statement = ((java.sql.Connection) connection).prepareStatement(sql)) {
            statement.setString(1, username_email);
            statement.setString(2, username_email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long userId = resultSet.getLong("id_user");
                String fname = resultSet.getString("fname_user");
                String lname = resultSet.getString("lname_user");
                LocalDate birthdate = resultSet.getDate("birthdate_user").toLocalDate();
                String username = resultSet.getString("username_user");
                String email = resultSet.getString("email_user");
                String password_user_db = resultSet.getString("password_user");
                Nationality nationality = new Nationality(resultSet.getLong("nationality_user"), resultSet.getString("nationality"));
                Boolean isDeleted = resultSet.getBoolean("isDeleted");
                
                Encrypt encryptor = new Encrypt();

                if(encryptor.matches(password, password_user_db)) {
                	user = new User(userId, fname, lname, birthdate, username, email, password, nationality, isDeleted);
                }
            }
        } catch (SQLException e) {
            // Handle the exception appropriately
        }
        return user;
	}
	
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();

        connectDB.connect(); // Establish the database connection

        java.sql.Connection connection = connectDB.getConnection();
        String sql = "SELECT U.id_user, U.fname_user, U.lname_user, U.birthdate_user, U.username_user, U.email_user, U.password_user, U.nationality_user, N.nationality, U.isDeleted FROM Users U JOIN nationalities N ON U.nationality_user = N.id_nationality";
        try (PreparedStatement statement = ((java.sql.Connection) connection).prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
        	while (resultSet.next()) {
        	    Long id = resultSet.getLong("id_user");
        	    String fname = resultSet.getString("fname_user");
        	    String lname = resultSet.getString("lname_user");
        	    LocalDate birthdate = resultSet.getDate("birthdate_user").toLocalDate();
        	    String username = resultSet.getString("username_user");
        	    String email = resultSet.getString("email_user");
        	    String password = resultSet.getString("password_user");
        	    Nationality nationality = new Nationality(resultSet.getLong("nationality_user"), resultSet.getString("nationality"));
        	    Boolean isDeleted = resultSet.getBoolean("isDeleted");

        	    User user = new User(id, fname, lname, birthdate, username, email, password, nationality, isDeleted);
        	    users.add(user);
        	}
        }

        connectDB.disconnect(); // Close the database connection

        return users;
    }

    public User getUserById(Long id) throws SQLException {
        User user = null;

        connectDB.connect(); // Establish the database connection

        java.sql.Connection connection = connectDB.getConnection();
        String sql = "SELECT U.id_user, U.fname_user, U.lname_user, U.birthdate_user, U.username_user, U.email_user, U.password_user, U.nationality_user, N.nationality, U.isDeleted FROM Users U JOIN nationalities N ON U.nationality_user = N.id_nationality WHERE id_user = ? AND isDeleted = 0";
        try (PreparedStatement statement = ((java.sql.Connection) connection).prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long userId = resultSet.getLong("id_user");
                String fname = resultSet.getString("fname_user");
                String lname = resultSet.getString("lname_user");
                LocalDate birthdate = resultSet.getDate("birthdate_user").toLocalDate();
                String username = resultSet.getString("username_user");
                String email = resultSet.getString("email_user");
                String password = resultSet.getString("password_user");
                Nationality nationality = new Nationality(resultSet.getLong("nationality_user"), resultSet.getString("nationality"));
                Boolean isDeleted = resultSet.getBoolean("isDeleted");

                user = new User(userId, fname, lname, birthdate, username, email, password, nationality, isDeleted);
            }
        } catch (SQLException e) {
            // Handle the exception appropriately
        }

        connectDB.disconnect(); // Close the database connection

        return user;
    }

    public User createUser(User user) throws SQLException {
        connectDB.connect(); // Establish the database connection

        java.sql.Connection connection = connectDB.getConnection();
        String sql = "INSERT INTO Users (fname_user, lname_user, birthdate_user, username_user, email_user, password_user, nationality_user, isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFname());
            statement.setString(2, user.getLname());
            statement.setDate(3, java.sql.Date.valueOf(user.getBirthdate()));
            statement.setString(4, user.getUsername());
            statement.setString(5, user.getEmail());
            
            Encrypt encryptor = new Encrypt();
            String encryptedPassword = encryptor.encryptPassword(user.getPassword());
            statement.setString(6, encryptedPassword);
            
            
            statement.setLong(7, user.getNationality().getId());
            statement.setBoolean(8, user.getIsDeleted());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                // User was successfully created in the database
                return user;
            }
        } catch (SQLException e) {
            // Handle the exception appropriately
        }

        connectDB.disconnect(); // Close the database connection

        // Failed to create the user in the database
        return null;
    }

    public User updateUser(Long id, User updatedUser) throws SQLException {
        User existingUser = getUserById(id);
        if (existingUser != null) {
            existingUser.setFname(updatedUser.getFname());
            existingUser.setLname(updatedUser.getLname());
            existingUser.setBirthdate(updatedUser.getBirthdate());
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setNationality(updatedUser.getNationality());
            existingUser.setIsDeleted(updatedUser.getIsDeleted());

            connectDB.connect(); // Establish the database connection

            java.sql.Connection connection = connectDB.getConnection();
            String sql = "UPDATE users SET fname_user = ?, lname_user = ?, birthdate_user = ?, username_user = ?, email_user = ?, password_user = ?, nationality_user = ?, isDeleted = ? WHERE id_user = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, existingUser.getFname());
                statement.setString(2, existingUser.getLname());
                statement.setDate(3, java.sql.Date.valueOf(existingUser.getBirthdate()));
                statement.setString(4, existingUser.getUsername());
                statement.setString(5, existingUser.getEmail());
                
                Encrypt encryptor = new Encrypt();
                String encryptedPassword = encryptor.encryptPassword(existingUser.getPassword());
                statement.setString(6, encryptedPassword);
                
                statement.setLong(7, existingUser.getNationality().getId());
                statement.setBoolean(8, existingUser.getIsDeleted());
                statement.setLong(9, id);
                statement.executeUpdate();
            }

            connectDB.disconnect(); // Close the database connection

            return existingUser;
        }
        return null;
    }

    public void deleteUser(Long id) throws SQLException {
        connectDB.connect(); // Establish the database connection
        
        java.sql.Connection connection = connectDB.getConnection();
        String sql = "UPDATE Users SET isDeleted = ? WHERE id_user = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
        	statement.setBoolean(1, true);
            statement.setLong(2, id);
            statement.executeUpdate();
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // User deleted successfully
            }
        }
        
        connectDB.disconnect(); // Close the database connection
    }

                                          /* CARS SERVICES */
    
    public List<Car> getAllCars() throws SQLException {
        List<Car> cars = new ArrayList<>();

        connectDB.connect(); // Establish the database connection

        java.sql.Connection connection = connectDB.getConnection();
        String sql = "SELECT * FROM Cars";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id_car");
                String registrationPlate = resultSet.getString("registration_plate_car");
                String nameCar = resultSet.getString("name_car");
                Boolean isDeleted = resultSet.getBoolean("isDeleted");
                
                Car car = new Car(id, registrationPlate, nameCar, isDeleted);
                cars.add(car);
            }
        }

        connectDB.disconnect(); // Close the database connection

        return cars;
    }

    public Car getCarById(Long id) throws SQLException {
        Car car = null;

        connectDB.connect(); // Establish the database connection

        java.sql.Connection connection = connectDB.getConnection();
        String sql = "SELECT * FROM Cars WHERE id_car = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String registrationPlate = resultSet.getString("registration_plate_car");
                String nameCar = resultSet.getString("name_car");
                Boolean isDeleted = resultSet.getBoolean("isDeleted");

                car = new Car(id, registrationPlate, nameCar, isDeleted);
            }
        }

        connectDB.disconnect(); // Close the database connection

        return car;
    }

    public Car createCar(Car car) throws SQLException {
        connectDB.connect(); // Establish the database connection

        java.sql.Connection connection = connectDB.getConnection();
        String sql = "INSERT INTO Cars (registration_plate_car, name_car, isDeleted) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
        	statement.setString(1, car.getRegistrationPlate());
            statement.setString(2, car.getNameCar());
            statement.setBoolean(3, car.getIsDeleted());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Car inserted successfully
                return car;
            }
        }

        connectDB.disconnect(); // Close the database connection

        return null;
    }

    public Car updateCar(Long id, Car updatedCar) throws SQLException {
        Car existingCar = getCarById(id);
        if (existingCar != null) {
            existingCar.setRegistrationPlate(updatedCar.getRegistrationPlate());
            existingCar.setNameCar(updatedCar.getNameCar());

            connectDB.connect(); // Establish the database connection

            java.sql.Connection connection = connectDB.getConnection();
            String sql = "UPDATE Cars SET registration_plate_car = ?, name_car = ? WHERE id_car = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, existingCar.getRegistrationPlate());
                statement.setString(2, existingCar.getNameCar());
                statement.setLong(3, id);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    // Car updated successfully
                    return existingCar;
                }
            }

            connectDB.disconnect(); // Close the database connection
        }
        return null;
    }

    public void deleteCar(Long id) throws SQLException {
        connectDB.connect(); // Establish the database connection

        java.sql.Connection connection = connectDB.getConnection();
        String sql = "UPDATE Cars SET isDeleted = ? WHERE id_car = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, true);
            statement.setLong(2, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Car deleted successfully
            }
        }

        connectDB.disconnect(); // Close the database connection
    }

                                       /* LOCATIONS SERVICES */
    
    public List<Location> getAllLocations() throws SQLException {
        List<Location> locations = new ArrayList<>();
        
        connectDB.connect(); // Establish the database connection
        
        java.sql.Connection connection = connectDB.getConnection();

        // Execute the query
        String sql = "SELECT L.id_location, L.latitude_location, L.longitude_location, L.date_location, L.time_location, L.id_car, C.registration_plate_car, C.name_car, C.isDeleted FROM locations L JOIN cars C ON L.id_car = C.id_car WHERE C.isDeleted = 'false'";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id_location");
                String latitude = resultSet.getString("latitude_location");
                String longitude = resultSet.getString("longitude_location");
                LocalDate date = resultSet.getDate("date_location").toLocalDate();
                LocalTime time = resultSet.getTime("time_location").toLocalTime();
                Car car = new Car(resultSet.getLong("id_car"), resultSet.getString("registration_plate_car"), resultSet.getString("name_car"), resultSet.getBoolean("isDeleted"));

                Location location = new Location(id, NumberUtils.parseNumber(latitude, BigDecimal.class), NumberUtils.parseNumber(longitude, BigDecimal.class), date, time, car);
                locations.add(location);
            }
        }

        // Close the database connection
        connectDB.disconnect();

        return locations;
    }

    public Location getLocationByRegistrationPlate(String plate) throws SQLException {
        Location location = null;

        connectDB.connect(); // Establish the database connection
        
        java.sql.Connection connection = connectDB.getConnection();

        // Prepare the select statement
        String sql = "SELECT L.id_location, L.latitude_location, L.longitude_location, L.date_location, L.time_location, L.id_car, C.registration_plate_car, C.name_car, C.isDeleted FROM locations L JOIN cars C ON L.id_car = C.id_car WHERE C.registration_plate_car = ? AND C.isDeleted = 'false'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set the parameter for the select statement
            statement.setString(1, plate);

            // Execute the select statement
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id_location");
                    BigDecimal latitude = resultSet.getBigDecimal("latitude_location");
                    BigDecimal longitude = resultSet.getBigDecimal("longitude_location");
                    LocalDate date = resultSet.getDate("date_location").toLocalDate();
                    LocalTime time = resultSet.getTime("time_location").toLocalTime();
                    Long carId = resultSet.getLong("id_car");
                    String registrationPlate = resultSet.getString("registration_plate_car");
                    String carName = resultSet.getString("name_car");
                    boolean isCarDeleted = resultSet.getBoolean("isDeleted");

                    // Create the Cars object
                    Car car = new Car(carId, registrationPlate, carName, isCarDeleted);

                    // Create the Locations object
                    location = new Location(id, latitude, longitude, date, time, car);
                }
            }
        }

        // Close the database connection
        connectDB.disconnect();

        return location;
    }

    public Location createLocation(Location location) throws SQLException {
        connectDB.connect(); // Establish the database connection
        
        java.sql.Connection connection = connectDB.getConnection();

        // Prepare the insert statement
        String sql = "INSERT INTO locations (latitude_location, longitude_location, date_location, time_location, id_car) VALUES (?, ?, GETDATE(), (SELECT CONVERT(TIME(0),GETDATE())), (SELECT id_car FROM cars WHERE registration_plate_car = ?))";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set the values for the insert statement
            statement.setBigDecimal(1, location.getLatitude());
            statement.setBigDecimal(2, location.getLongitude());
            statement.setString(3, location.getCar().getRegistrationPlate());

            // Execute the insert statement
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
            	// Location was successfully created in the database
            	return location;
            }
        }

        // Close the database connection
        connectDB.disconnect();

        return null;
    }

    public Location updateLocation(String plate, Location updatedLocation) throws SQLException {
        Location existingLocation = getLocationByRegistrationPlate(plate);
        if (existingLocation != null) {
            existingLocation.setLatitude(updatedLocation.getLatitude());
            existingLocation.setLongitude(updatedLocation.getLongitude());

            connectDB.connect(); // Establish the database connection
            
            java.sql.Connection connection = connectDB.getConnection();

            // Prepare the update statement
            String sql = "UPDATE locations SET latitude_location = ?, longitude_location = ? WHERE id_location = (SELECT L.id_location FROM Locations L WHERE id_car = (SELECT C.id_car FROM Cars C WHERE C.registration_plate_car = ?))";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set the parameters for the update statement
                statement.setBigDecimal(1, existingLocation.getLatitude());
                statement.setBigDecimal(2, existingLocation.getLongitude());
                statement.setString(3, plate);

                // Execute the update statement
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    // Update successful
                    return existingLocation;
                }
            }

            // Close the database connection
            connectDB.disconnect();
        }
        return null;
    }

    public void deleteLocation(String plate) throws SQLException {
        connectDB.connect(); // Establish the database connection
        java.sql.Connection connection = connectDB.getConnection();

        // Prepare the delete statement
        String sql = "DELETE FROM Locations WHERE id_car = (SELECT C.id_car FROM Cars C WHERE C.registration_plate_car = ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set the parameter for the delete statement
            statement.setString(1, plate);

            // Execute the delete statement
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                // Deletion successful
            }
        }

        // Close the database connection
        connectDB.disconnect();
    }
}
