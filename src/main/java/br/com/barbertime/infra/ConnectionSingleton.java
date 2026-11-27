package br.com.barbertime.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionSingleton {

    private static Connection connection;

    private ConnectionSingleton() {
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = System.getenv().getOrDefault(
                    "DB_URL", "jdbc:postgresql://localhost:5432/barbertime_web");
            String user = System.getenv().getOrDefault("DB_USER", "postgres");
            String password = System.getenv().getOrDefault("DB_PASSWORD", "1234");

            try {
                // garante o registro do driver mesmo quando rodando via exec:java
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver do PostgreSQL não encontrado no classpath.", e);
            }
            connection = DriverManager.getConnection(url, user, password);
        }

        return connection;
    }
}
