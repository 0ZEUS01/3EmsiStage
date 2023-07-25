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
import CarFleet.Model.Location_History;

@Service
public class CarFleetServiceLocationHistory {
	
                                 /* CONSTRUCTOR INITIALIZE ConnectDB SERVICES */
	
	private ConnectDB connectDB;
	private final CarFleetLocationWebSocketHandler webSocketHandler;
	
	public CarFleetServiceLocationHistory(CarFleetLocationWebSocketHandler webSocketHandler) {
		connectDB = new ConnectDB();
		this.webSocketHandler = webSocketHandler;
	}
	
	                                    /* LOCATIONS HISTORY SERVICES */
	
	public List<Location_History> getAllLocationsHistory() throws SQLException{
		List<Location_History> locations = new ArrayList<>();

		connectDB.connect(); // Establish the database connection

		java.sql.Connection connection = connectDB.getConnection();

		// Execute the query
		String sql = "SELECT L.id_history, L.latitude_history, L.longitude_history, L.date_history, L.time_history, L.id_car, C.registration_plate_car, C.name_car, C.isDeleted FROM locations_history L JOIN cars C ON L.id_car = C.id_car WHERE C.isDeleted = 'false'";
		try (PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Long id = resultSet.getLong("id_history");
				String latitude = resultSet.getString("latitude_history");
				String longitude = resultSet.getString("longitude_history");
				LocalDate date = resultSet.getDate("date_history").toLocalDate();
				LocalTime time = resultSet.getTime("time_history").toLocalTime();
				Car car = new Car(resultSet.getLong("id_car"), resultSet.getString("registration_plate_car"),
						resultSet.getString("name_car"), resultSet.getBoolean("isDeleted"));

				Location_History location = new Location_History(id, NumberUtils.parseNumber(latitude, BigDecimal.class),
						NumberUtils.parseNumber(longitude, BigDecimal.class), date, time, car);
				locations.add(location);
			}
		}

		// Close the database connection
		connectDB.disconnect();

		return locations;
	}
	
	public List<Location_History> getLocationHistoryByRegistrationPlate(String plate) throws SQLException {
	    List<Location_History> locations = new ArrayList<>();

	    connectDB.connect(); // Establish the database connection

	    java.sql.Connection connection = connectDB.getConnection();

	    // Prepare the select statement
	    String sql = "SELECT L.id_history, L.latitude_history, L.longitude_history, L.date_history, L.time_history, L.id_car, C.registration_plate_car, C.name_car, C.isDeleted FROM locations_history L JOIN cars C ON L.id_car = C.id_car WHERE C.registration_plate_car = ? AND C.isDeleted = 'false'";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        // Set the parameter for the select statement
	        statement.setString(1, plate);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Long id = resultSet.getLong("id_history");
	                String latitude = resultSet.getString("latitude_history");
	                String longitude = resultSet.getString("longitude_history");
	                LocalDate date = resultSet.getDate("date_history").toLocalDate();
	                LocalTime time = resultSet.getTime("time_history").toLocalTime();
	                Car car = new Car(resultSet.getLong("id_car"), resultSet.getString("registration_plate_car"),
	                        resultSet.getString("name_car"), resultSet.getBoolean("isDeleted"));

	                Location_History location = new Location_History(id, NumberUtils.parseNumber(latitude, BigDecimal.class),
	                        NumberUtils.parseNumber(longitude, BigDecimal.class), date, time, car);
	                locations.add(location);
	            }
	        }
	    } catch (SQLException e) {
	        // Handle the exception if needed
	        e.printStackTrace();
	    } finally {
	        // Close the database connection in the finally block to ensure it's always closed
	        connectDB.disconnect();
	    }

	    return locations;
	}

}
