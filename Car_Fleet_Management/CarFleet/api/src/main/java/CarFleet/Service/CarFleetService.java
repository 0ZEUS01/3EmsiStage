package CarFleet.Service;

import org.hibernate.annotations.Nationalized;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Cache.Connection;
import org.springframework.stereotype.Service;
import CarFleet.Model.*;

import java.beans.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
	
    public List<Users> getAllUsers() throws SQLException {
        List<Users> users = new ArrayList<>();

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
        	    Nationalities nationality = new Nationalities(resultSet.getLong("nationality_user"), resultSet.getString("nationality"));
        	    Boolean isDeleted = resultSet.getBoolean("isDeleted");

        	    Users user = new Users(id, fname, lname, birthdate, username, email, password, nationality, isDeleted);
        	    users.add(user);
        	}
        }

        connectDB.disconnect(); // Close the database connection

        return users;
    }

    public Users getUserById(Long id) throws SQLException {
        Users user = null;

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
                Nationalities nationality = new Nationalities(resultSet.getLong("nationality_user"), resultSet.getString("nationality"));
                Boolean isDeleted = resultSet.getBoolean("isDeleted");

                user = new Users(userId, fname, lname, birthdate, username, email, password, nationality, isDeleted);
            }
        } catch (SQLException e) {
            // Handle the exception appropriately
        }

        connectDB.disconnect(); // Close the database connection

        return user;
    }

    public Users createUser(Users user) throws SQLException {
        connectDB.connect(); // Establish the database connection

        java.sql.Connection connection = connectDB.getConnection();
        String sql = "INSERT INTO Users (fname_user, lname_user, birthdate_user, username_user, email_user, password_user, nationality_user, isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFname());
            statement.setString(2, user.getLname());
            statement.setDate(3, java.sql.Date.valueOf(user.getBirthdate()));
            statement.setString(4, user.getUsername());
            statement.setString(5, user.getEmail());
            statement.setString(6, user.getPassword());
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

    public Users updateUser(Long id, Users updatedUser) throws SQLException {
        Users existingUser = getUserById(id);
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
                statement.setString(6, existingUser.getPassword());
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
    
    public List<Cars> getAllCars() throws SQLException {
        List<Cars> cars = new ArrayList<>();

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
                
                Cars car = new Cars(id, registrationPlate, nameCar, isDeleted);
                cars.add(car);
            }
        }

        connectDB.disconnect(); // Close the database connection

        return cars;
    }

    public Cars getCarById(Long id) throws SQLException {
        Cars car = null;

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

                car = new Cars(id, registrationPlate, nameCar, isDeleted);
            }
        }

        connectDB.disconnect(); // Close the database connection

        return car;
    }

    public Cars createCar(Cars car) throws SQLException {
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

    public Cars updateCar(Long id, Cars updatedCar) throws SQLException {
        Cars existingCar = getCarById(id);
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
