package connection;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectDataBase {

    private static ConnectDataBase instance;
    private Connection connection;

    public static ConnectDataBase getInstance() {
        if (instance == null) {
            instance = new ConnectDataBase();
        }
        return instance;
    }

    private ConnectDataBase() {

    }

    public void connectToDatabase() throws SQLException {
        String server = "DESKTOP-7EOCORJ";
        String port = "1433";
        String database = "ThuVien";
        String userName = "sa";
        String password = "123";
        String connectionUrl = "jdbc:sqlserver://DESKTOP-7EOCORJ:1433;databaseName=ThuVien;encrypt=false";
        connection = java.sql.DriverManager.getConnection(connectionUrl, userName, password);



        // Load the SQL Server JDBC Driver
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver not found.", e);
        }

        // Establish the connection
        connection = java.sql.DriverManager.getConnection(connectionUrl, userName, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
