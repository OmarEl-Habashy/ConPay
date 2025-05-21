package aammo.ppv.dao;

import aammo.ppv.model.User;
//import org.mindrot.jbcrypt .BCrypt;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcUserDAO implements UserDAO {
    private String jdbcURL ;
    private String jdbcUsername;
    private String jdbcPassword;

    // Basic CRUD SQL statements
    private static final String INSERT_USER_SQL = "INSERT INTO Users (Username, Email, PassW, Bio) VALUES (?, ?, ?, ?)";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM Users WHERE UserID = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM Users";
    private static final String DELETE_USER_SQL = "DELETE FROM Users WHERE UserID = ?";
    private static final String UPDATE_USER_SQL = "UPDATE Users SET Username = ?, Email = ?, PassW = ?, Bio = ? WHERE UserID = ?";

    // Additional SQL statements for social features
    private static final String SELECT_USER_BY_USERNAME = "SELECT * FROM Users WHERE Username = ?";
    private static final String COUNT_FOLLOWERS = "SELECT COUNT(*) FROM Follows WHERE FolloweeID = ?";
    private static final String COUNT_FOLLOWING = "SELECT COUNT(*) FROM Follows WHERE FollowerID = ?";
    private static final String CHECK_IS_FOLLOWING = "SELECT COUNT(*) FROM Follows WHERE FollowerID = ? AND FolloweeID = ?";
    private static final String INSERT_FOLLOW = "INSERT INTO Follows (FollowerID, FolloweeID) VALUES (?, ?)";
    private static final String DELETE_FOLLOW = "DELETE FROM Follows WHERE FollowerID = ? AND FolloweeID = ?";
    private static final String SELECT_FOLLOWERS =
            "SELECT u.* FROM Users u JOIN Follows f ON u.UserID = f.FollowerID WHERE f.FolloweeID = ?";
    private static final String SELECT_FOLLOWING =
            "SELECT u.* FROM Users u JOIN Follows f ON u.UserID = f.FolloweeID WHERE f.FollowerID = ?";



    public JdbcUserDAO() {
        loadProperties();
    }

    // Load connection properties from config file
    private void loadProperties() {
        Properties props = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                props.load(inputStream);
                jdbcURL = props.getProperty("db.url");
                jdbcUsername = props.getProperty("db.username");
                jdbcPassword = props.getProperty("db.password");
                System.out.println("Database configuration loaded successfully");
            } else {
                System.out.println("config.properties not found, using defaults");

            }
        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());

        }
    }

    public Connection getConnection() throws SQLException {
        try {
            // Explicitly load and register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    // Basic CRUD implementations
    @Override
    public void insertUser(User user) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getHashedPassword());
            preparedStatement.setString(4, user.getBio());

            preparedStatement.executeUpdate();

            // Get the auto-generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public User selectUser(int userId) throws SQLException {
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
        }
        return user;
    }

    @Override
    public List<User> selectAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }

    @Override
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

    @Override
    public boolean deleteUser(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL)) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    // Additional methods for social features
    @Override
    public User getUserByUsername(String username) throws SQLException {
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
        }
        return user;
    }

    @Override
    public int getFollowerCount(int userId) throws SQLException {
        int count = 0;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_FOLLOWERS)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
        return count;
    }

    @Override
    public int getFollowingCount(int userId) throws SQLException {
        int count = 0;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_FOLLOWING)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
        return count;
    }

    @Override
    public boolean isFollowing(int followerId, int followeeId) throws SQLException {
        boolean following = false;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_IS_FOLLOWING)) {
            preparedStatement.setInt(1, followerId);
            preparedStatement.setInt(2, followeeId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                following = rs.getInt(1) > 0;
            }
        }
        return following;
    }

    @Override
    public void followUser(int followerId, int followeeId) throws SQLException {
        // Prevent users from following themselves
        if (followerId == followeeId) {
            return;
        }

        // Check if already following
        if (isFollowing(followerId, followeeId)) {
            return;
        }

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_FOLLOW)) {
            preparedStatement.setInt(1, followerId);
            preparedStatement.setInt(2, followeeId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void unfollowUser(int followerId, int followeeId) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FOLLOW)) {
            preparedStatement.setInt(1, followerId);
            preparedStatement.setInt(2, followeeId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<User> getFollowers(int userId) throws SQLException {
        List<User> followers = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FOLLOWERS)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                followers.add(extractUserFromResultSet(rs));
            }
        }
        return followers;
    }

    @Override
    public List<User> getFollowing(int userId) throws SQLException {
        List<User> following = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FOLLOWING)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                following.add(extractUserFromResultSet(rs));
            }
        }
        return following;
    }

    // Helper method to extract a User from ResultSet
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("UserID");
        String username = rs.getString("Username");
        String email = rs.getString("Email");
        String password = rs.getString("PassW");
        String bio = rs.getString("Bio");

        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        LocalDateTime createdAt = null;
        if (createdAtTimestamp != null) {
            createdAt = createdAtTimestamp.toLocalDateTime();
        }

        return new User(id, username, email, password, bio, createdAt);
    }


    public User LoginUser(String username, String password) throws SQLException {
        User user = null;
        String query = "SELECT * FROM Users WHERE Username = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("PassW");

                    // Direct password comparison
                    if (password.equals(storedPassword)) {
                        user = extractUserFromResultSet(rs);
                    }
                }
            }
        }
        return user;
    }
    @Override
    public List<User> searchUsersByUsername(String query) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE Username LIKE ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                users.add(user);
            }
        }

        return users;
    }
}