package CarFleet.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import CarFleet.Model.ConnectDB;
import CarFleet.Model.Encrypt;
import CarFleet.Model.Nationality;
import CarFleet.Model.User;

@Service
public class CarFleetServiceUser {

	                              /* CONSTRUCTOR INITIALIZE ConnectDB SERVICES */

	private ConnectDB connectDB;

	public CarFleetServiceUser() {
		connectDB = new ConnectDB();
	}

	                                           /* USERS SERVICES */

	public User loginUser(String username_email, String password) throws SQLException {
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
				Nationality nationality = new Nationality(resultSet.getLong("nationality_user"),
						resultSet.getString("nationality"));
				Boolean isDeleted = resultSet.getBoolean("isDeleted");

				Encrypt encryptor = new Encrypt();

				if (encryptor.matches(password, password_user_db)) {
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
				Nationality nationality = new Nationality(resultSet.getLong("nationality_user"),
						resultSet.getString("nationality"));
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
				Nationality nationality = new Nationality(resultSet.getLong("nationality_user"),
						resultSet.getString("nationality"));
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
	
	public String recoverUser(String username_email) throws SQLException{
	    String email = "";
	    connectDB.connect(); // Establish the database connection
	    
	    java.sql.Connection connection = connectDB.getConnection();
	    
	    Encrypt encryptor = new Encrypt();
	    String noEncryptedPassword = encryptor.generateRandomPassword(8);
	    String newEncryptedPassword = encryptor.encryptPassword(noEncryptedPassword);
	    
	    String sql = "UPDATE users SET password_user = ? WHERE (username_user = ? OR email_user = ?)";
	    
	    try (PreparedStatement statement = connection.prepareStatement(sql)){
	        statement.setString(1, newEncryptedPassword);
	        statement.setString(2, username_email);
	        statement.setString(3, username_email);
	        int rowsAffected = statement.executeUpdate();
	        if (rowsAffected > 0) {
	            // User password updated successfully
	            // Use a different connection for the SELECT query
	            connectDB.connect(); // Establish the database connection

	            java.sql.Connection connection_two = connectDB.getConnection();
	            
	            String sql_two = "SELECT U.email_user FROM users U WHERE (U.username_user = ? OR U.email_user = ?)";
	            
	            try (PreparedStatement statement_two = connection_two.prepareStatement(sql_two)) {
	                statement_two.setString(1, username_email);
	                statement_two.setString(2, username_email);
	                ResultSet resultSet = statement_two.executeQuery();
	                if (resultSet.next()) {
	                    email = resultSet.getString("email_user");
	                }
	            } catch (SQLException e) {
	                // Handle the exception appropriately
	            }
	        }
	    }
	    
	    connectDB.disconnect(); // Close the database connection
	    return email + "|" + noEncryptedPassword;
	}
}
