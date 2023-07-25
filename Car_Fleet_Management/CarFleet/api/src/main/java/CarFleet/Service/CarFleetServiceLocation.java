package CarFleet.Service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import CarFleet.Config.CarFleetLocationWebSocketHandler;
import CarFleet.Model.Car;
import CarFleet.Model.ConnectDB;
import CarFleet.Model.Location;

@Service
public class CarFleetServiceLocation {

	                                 /* CONSTRUCTOR INITIALIZE ConnectDB SERVICES */

	private ConnectDB connectDB;
	private final CarFleetLocationWebSocketHandler webSocketHandler;

	public CarFleetServiceLocation(CarFleetLocationWebSocketHandler webSocketHandler) {
		connectDB = new ConnectDB();
		this.webSocketHandler = webSocketHandler;
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
				Car car = new Car(resultSet.getLong("id_car"), resultSet.getString("registration_plate_car"),
						resultSet.getString("name_car"), resultSet.getBoolean("isDeleted"));

				Location location = new Location(id, NumberUtils.parseNumber(latitude, BigDecimal.class),
						NumberUtils.parseNumber(longitude, BigDecimal.class), date, time, car);
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
			String sql = "UPDATE locations SET latitude_location = ?, longitude_location = ?, date_location = GETDATE(), time_location = (SELECT CONVERT(TIME(0),GETDATE())) WHERE id_location = (SELECT L.id_location FROM Locations L WHERE id_car = (SELECT C.id_car FROM Cars C WHERE C.registration_plate_car = ?))";
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
	
	public void updateLocationRealTime(String plate, Location updatedLocation) throws SQLException {
	    updateLocation(plate, updatedLocation);
	    System.out.println(plate);
	    System.out.println(updatedLocation.getLatitude());
	    // Broadcast the updated location to connected WebSocket clients
	    try {
	        webSocketHandler.broadcastLocationUpdate(updatedLocation);
	    } catch (Exception e) {
	        // Handle exceptions if broadcasting fails
	    }
	}
}
