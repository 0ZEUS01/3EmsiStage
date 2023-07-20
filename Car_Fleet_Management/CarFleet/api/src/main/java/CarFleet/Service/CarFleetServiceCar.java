package CarFleet.Service;

import org.springframework.stereotype.Service;
import CarFleet.Model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarFleetServiceCar {
	
	                         /* CONSTRUCTOR INITIALIZE ConnectDB SERVICES */
	
	private ConnectDB connectDB;
	
	public CarFleetServiceCar() {
        connectDB = new ConnectDB();
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
}
