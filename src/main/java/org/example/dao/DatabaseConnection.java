package org.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Single place that opens SQLite connections.
 * Every connection has foreign key enforcement switched on, because
 * SQLite disables foreign keys by default on each new connection.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:clinic.db";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            connection.close();
            throw e;
        }

        return connection;
    }
}
