package aammo.ppv.controller;

import aammo.ppv.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private final String jdbcURL = "jdbc:mysql://localhost:3306/ConPay";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "your_password"; // Replace with your actual password

    private static final String INSERT_USER_SQL = "INSERT INTO Users (Username, Email, PassW, Bio) VALUES (?, ?, ?, ?)";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM Users WHERE UserID = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM Users";
    private static final String DELETE_USER_SQL = "DELETE FROM Users WHERE UserID = ?";
    private static final String UPDATE_USER_SQL = "UPDATE Users SET Username = ?, Email = ?, PassW = ?, Bio = ? WHERE UserID = ?";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    // Create (Insert)
    public void insertUser(User user) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getHashedPassword());
            preparedStatement.setString(4, user.getBio());
            preparedStatement.executeUpdate();
        }
    }

    // Read (Select one user)
    public User selectUser(int userId) throws SQLException {
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String username = rs.getString("Username");
                String email = rs.getString("Email");
                String password = rs.getString("PassW");
                String bio = rs.getString("Bio");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");

                LocalDateTime createdAtDateTime = createdAt.toLocalDateTime();
                user = new User(userId, username, email, password, bio, createdAtDateTime);
            }
        }
        return user;
    }

    // Read all users
    public List<User> selectAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("UserID");
                String username = rs.getString("Username");
                String email = rs.getString("Email");
                String password = rs.getString("PassW");
                String bio = rs.getString("Bio");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");

                LocalDateTime createdAtDateTime = createdAt.toLocalDateTime();
                users.add(new User(id, username, email, password, bio, createdAtDateTime));
            }
        }
        return users;
    }

    // Update
    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getHashedPassword());
            statement.setString(4, user.getBio());
            statement.setInt(5, user.getUserId());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    // Delete
    public boolean deleteUser(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL)) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }
}
