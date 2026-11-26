package br.com.barbertime.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSingleton {

    private static Connection connection;

    private ConnectionSingleton() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:postgresql://localhost:5432/barbertime";
            String user = "postgres";
            String password = "1234";

            connection = DriverManager.getConnection(url, user, password);
        }

        return connection;
    }
}