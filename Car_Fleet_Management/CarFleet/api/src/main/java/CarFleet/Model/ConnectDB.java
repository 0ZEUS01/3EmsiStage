package CarFleet.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	private static final String URL = "jdbc:sqlserver://localhost;databaseName=Car_Fleet;sslProtocol=TLSv1.2;encrypt=false";
    private static final String USERNAME = "zeus";
    private static final String PASSWORD = "zeus";
    
    private Connection connection;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
