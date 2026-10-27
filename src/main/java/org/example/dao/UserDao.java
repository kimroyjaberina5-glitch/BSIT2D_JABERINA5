package org.example.dao;

import org.example.model.Role;
import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    /** Inserts a new account with status PENDING. Returns false if the username is taken. */
    public boolean register(
            String username,
            String password,
            Role role) throws SQLException {

        String sql = """
                INSERT INTO users (username, password, role, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role.name());

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            if (e.getMessage() != null
                    && e.getMessage().toLowerCase().contains("unique")) {
                return false;
            }
            throw e;
        }
    }

    /** Returns the matching user, or null if the credentials are wrong. */
    public User login(String username, String password) throws SQLException {

        String sql = """
                SELECT user_id, username, password, role, status
                FROM users
                WHERE username = ? AND password = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new User(
                            result.getInt("user_id"),
                            result.getString("username"),
                            result.getString("password"),
                            result.getString("role"),
                            result.getString("status")
                    );
                }
            }
        }

        return null;
    }

    /** Returns every registered account, regardless of role or status. */
    public List<User> getAllUsers() throws SQLException {
        String sql = """
                SELECT user_id, username, password, role, status
                FROM users
                ORDER BY user_id
                """;

        return queryUsers(sql, null);
    }

    /** Returns every account with status PENDING, across all roles. */
    public List<User> getPendingUsers() throws SQLException {
        String sql = """
                SELECT user_id, username, password, role, status
                FROM users
                WHERE status = ?
                ORDER BY user_id
                """;

        return queryUsers(sql, "PENDING");
    }

    /**
     * Changes a PENDING account to APPROVED.
     * Returns false if no pending account has that ID.
     */
    public boolean approveUser(int userId) throws SQLException {
        String sql = """
                UPDATE users
                SET status = 'APPROVED'
                WHERE user_id = ? AND status = 'PENDING'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            return statement.executeUpdate() == 1;
        }
    }

    private List<User> queryUsers(String sql, String statusParam) throws SQLException {
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (statusParam != null) {
                statement.setString(1, statusParam);
            }

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    users.add(new User(
                            result.getInt("user_id"),
                            result.getString("username"),
                            result.getString("password"),
                            result.getString("role"),
                            result.getString("status")
                    ));
                }
            }
        }

        return users;
    }
}
