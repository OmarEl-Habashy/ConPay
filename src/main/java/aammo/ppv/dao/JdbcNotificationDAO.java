// src/main/java/aammo/ppv/dao/JdbcNotificationDAO.java
package aammo.ppv.dao;

import aammo.ppv.model.Notification;
import aammo.ppv.model.NotificationType;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcNotificationDAO implements NotificationDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;

    private static final String INSERT_NOTIFICATION =
            "INSERT INTO Notifications (RecipientID, SenderID, Type, ReferenceID, Content) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_NOTIFICATION =
            "SELECT * FROM Notifications WHERE NotificationID = ?";
    private static final String SELECT_USER_NOTIFICATIONS =
            "SELECT * FROM Notifications WHERE RecipientID = ? ORDER BY CreatedAt DESC";
    private static final String SELECT_UNREAD_NOTIFICATIONS =
            "SELECT * FROM Notifications WHERE RecipientID = ? AND IsRead = FALSE ORDER BY CreatedAt DESC";
    private static final String MARK_AS_READ =
            "UPDATE Notifications SET IsRead = TRUE WHERE NotificationID = ?";
    private static final String MARK_ALL_AS_READ =
            "UPDATE Notifications SET IsRead = TRUE WHERE RecipientID = ? AND IsRead = FALSE";
    private static final String DELETE_NOTIFICATION =
            "DELETE FROM Notifications WHERE NotificationID = ?";

    public JdbcNotificationDAO() {
        loadProperties();
    }

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

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    @Override
    public void createNotification(Notification notification) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     INSERT_NOTIFICATION, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, notification.getRecipientId());
            preparedStatement.setInt(2, notification.getSenderId());
            preparedStatement.setString(3, notification.getType().toString());
            preparedStatement.setInt(4, notification.getReferenceId());
            preparedStatement.setString(5, notification.getContent());

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notification.setNotificationId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Notification getNotification(int notificationId) throws SQLException {
        Notification notification = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_NOTIFICATION)) {

            preparedStatement.setInt(1, notificationId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                notification = extractNotificationFromResultSet(rs);
            }
        }
        return notification;
    }

    @Override
    public List<Notification> getUserNotifications(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_NOTIFICATIONS)) {

            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        }
        return notifications;
    }

    @Override
    public List<Notification> getUnreadNotifications(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_UNREAD_NOTIFICATIONS)) {

            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        }
        return notifications;
    }

    @Override
    public boolean markAsRead(int notificationId) throws SQLException {
        boolean updated;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(MARK_AS_READ)) {

            preparedStatement.setInt(1, notificationId);
            updated = preparedStatement.executeUpdate() > 0;
        }
        return updated;
    }

    @Override
    public boolean markAllAsRead(int userId) throws SQLException {
        boolean updated;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(MARK_ALL_AS_READ)) {

            preparedStatement.setInt(1, userId);
            updated = preparedStatement.executeUpdate() > 0;
        }
        return updated;
    }

    @Override
    public boolean deleteNotification(int notificationId) throws SQLException {
        boolean deleted;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_NOTIFICATION)) {

            preparedStatement.setInt(1, notificationId);
            deleted = preparedStatement.executeUpdate() > 0;
        }
        return deleted;
    }

    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("NotificationID");
        int recipientId = rs.getInt("RecipientID");
        int senderId = rs.getInt("SenderID");
        String typeStr = rs.getString("Type");
        NotificationType type = NotificationType.valueOf(typeStr);
        int referenceId = rs.getInt("ReferenceID");
        String content = rs.getString("Content");
        boolean isRead = rs.getBoolean("IsRead");

        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        LocalDateTime createdAt = null;
        if (createdAtTimestamp != null) {
            createdAt = createdAtTimestamp.toLocalDateTime();
        }

        return new Notification(id, recipientId, senderId, type, referenceId,
                content, isRead, createdAt);
    }
}