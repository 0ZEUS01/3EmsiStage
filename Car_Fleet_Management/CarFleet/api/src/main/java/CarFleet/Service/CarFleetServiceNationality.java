package CarFleet.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import CarFleet.Model.ConnectDB;
import CarFleet.Model.Nationality;

@Service
public class CarFleetServiceNationality {
	
	                              /* CONSTRUCTOR INITIALIZE ConnectDB SERVICES */
	
	private ConnectDB connectDB;

	public CarFleetServiceNationality() {
		connectDB = new ConnectDB();
	}
	
	public List<Nationality> getAllNationality() throws SQLException{
		List<Nationality> Nationalities = new ArrayList<>();

		connectDB.connect(); // Establish the database connection

		java.sql.Connection connection = connectDB.getConnection();
		String sql = "SELECT N.id_nationality, N.nationality FROM nationalities N";
		try (PreparedStatement statement = ((java.sql.Connection) connection).prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Long id = resultSet.getLong("id_nationality");
				String nationality_name = resultSet.getString("nationality");
				
				Nationality nationality = new Nationality(id, nationality_name);
				Nationalities.add(nationality);
			}
		}

		connectDB.disconnect(); // Close the database connection

		return Nationalities;
	}

}
