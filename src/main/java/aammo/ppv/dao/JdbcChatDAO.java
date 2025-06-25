package aammo.ppv.dao;
import aammo.ppv.dao.ChatDAO;
import aammo.ppv.model.ChatMessage;
import aammo.ppv.model.ChatRoom;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcChatDAO implements ChatDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;

    public JdbcChatDAO() {
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

    public Connection getConnection() throws SQLException {
        try {
            // Explicitly load and register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    @Override
    public ChatRoom createRoom(String name) throws SQLException {
        String sql = "INSERT INTO ChatRoom (Name) VALUES (?)";
        ChatRoom room = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int roomId = generatedKeys.getInt(1);
                        room = new ChatRoom(roomId, name, LocalDateTime.now());
                    }
                }
            }
        }

        return room;
    }

    @Override
    public void addUserToRoom(int userId, int roomId) throws SQLException {
        String sql = "INSERT INTO UserChatRoom (UserID, RoomID) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            // User is already in the room, ignore
            System.out.println("User is already in room " + roomId);
        }
    }

    @Override
    public void removeUserFromRoom(int userId, int roomId) throws SQLException {
        String sql = "DELETE FROM UserChatRoom WHERE UserID = ? AND RoomID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        }
    }

    @Override
    public ChatMessage saveMessage(ChatMessage message) throws SQLException {
        String sql = "INSERT INTO ChatMessage (RoomID, SenderID, Content) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, message.getRoomId());
            stmt.setInt(2, message.getSenderId());
            stmt.setString(3, message.getContent());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        message.setMessageId(generatedKeys.getInt(1));
                        message.setCreatedAt(LocalDateTime.now());
                    }
                }
            }
        }

        return message;
    }

    @Override
    public List<ChatMessage> getRoomMessages(int roomId, int limit, int offset) throws SQLException {
        String sql = "SELECT m.MessageID, m.RoomID, m.SenderID, m.Content, m.CreatedAt, u.Username " +
                "FROM ChatMessage m " +
                "JOIN Users u ON m.SenderID = u.UserID " +
                "WHERE m.RoomID = ? " +
                "ORDER BY m.CreatedAt DESC " +
                "LIMIT ? OFFSET ?";

        List<ChatMessage> messages = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChatMessage message = new ChatMessage();
                    message.setMessageId(rs.getInt("MessageID"));
                    message.setRoomId(rs.getInt("RoomID"));
                    message.setSenderId(rs.getInt("SenderID"));
                    message.setSenderName(rs.getString("Username"));
                    message.setContent(rs.getString("Content"));
                    message.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                    message.setType("CHAT"); // Default type for stored messages

                    messages.add(message);
                }
            }
        }

        return messages;
    }

    @Override
    public List<ChatRoom> getUserRooms(int userId) throws SQLException {
        String sql = "SELECT r.RoomID, r.Name, r.CreatedAt " +
                "FROM ChatRoom r " +
                "JOIN UserChatRoom ucr ON r.RoomID = ucr.RoomID " +
                "WHERE ucr.UserID = ? " +
                "ORDER BY r.CreatedAt DESC";

        List<ChatRoom> rooms = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChatRoom room = new ChatRoom();
                    room.setRoomId(rs.getInt("RoomID"));
                    room.setName(rs.getString("Name"));
                    room.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());

                    rooms.add(room);
                }
            }
        }

        return rooms;
    }

    @Override
    public ChatRoom getRoomById(int roomId) throws SQLException {
        String sql = "SELECT RoomID, Name, CreatedAt FROM ChatRoom WHERE RoomID = ?";
        ChatRoom room = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    room = new ChatRoom();
                    room.setRoomId(rs.getInt("RoomID"));
                    room.setName(rs.getString("Name"));
                    room.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                }
            }
        }

        return room;
    }

    @Override
    public boolean isUserInRoom(int userId, int roomId) throws SQLException {
        String sql = "SELECT 1 FROM UserChatRoom WHERE UserID = ? AND RoomID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, roomId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}