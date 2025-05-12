package aammo.ppv.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import aammo.ppv.model.Comment;
import aammo.ppv.model.Post;

public class JdbcPostDAO implements PostDAO {

    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;

    // SQL query strings
    private static final String INSERT_POST_SQL =
            "INSERT INTO Posts (UserID, ContentURL, Caption) VALUES (?, ?, ?)";
    private static final String SELECT_POST_BY_ID =
            "SELECT * FROM Posts WHERE PostID = ?";
    private static final String SELECT_POSTS_BY_USER_ID =
            "SELECT * FROM Posts WHERE UserID = ? ORDER BY CreatedAt DESC";
    private static final String SELECT_ALL_POSTS =
            "SELECT * FROM Posts ORDER BY CreatedAt DESC LIMIT ?, ?";
    private static final String SELECT_FEED_POSTS =
            "SELECT p.*, u.Username FROM Posts p " +
                    "JOIN Users u ON p.UserID = u.UserID " +
                    "WHERE p.UserID = ? " +
                    "   OR p.UserID IN (SELECT FolloweeID FROM Follows WHERE FollowerID = ?) " +
                    "ORDER BY p.CreatedAt DESC LIMIT ?, ?";
    private static final String UPDATE_POST_SQL =
            "UPDATE Posts SET ContentURL = ?, Caption = ? WHERE PostID = ?";
    private static final String DELETE_POST_SQL =
            "DELETE FROM Posts WHERE PostID = ?";
    private static final String COUNT_POSTS_BY_USER_ID =
            "SELECT COUNT(*) FROM Posts WHERE UserID = ?";
    private static final String SELECT_POSTS_BY_HASHTAG =
            "SELECT p.* FROM Posts p " +
                    "JOIN PostHashtags ph ON p.PostID = ph.PostID " +
                    "JOIN Hashtags h ON ph.HashtagID = h.HashtagID " +
                    "WHERE h.HashtagName = ? " +
                    "ORDER BY p.CreatedAt DESC LIMIT ?, ?";

    // SQL for fetching comments with username
    private static final String SELECT_COMMENTS_BY_POST_ID =
            "SELECT c.*, u.Username FROM Comments c " +
                    "JOIN Users u ON c.UserID = u.UserID " +
                    "WHERE c.PostID = ? ORDER BY c.CreatedAt ASC";

    // SQL for counting likes on a post
    private static final String COUNT_LIKES_BY_POST_ID =
            "SELECT COUNT(*) FROM Likes WHERE PostID = ?";

    // SQL for checking if a user has liked a post
    private static final String CHECK_USER_LIKED_POST =
            "SELECT COUNT(*) FROM Likes WHERE PostID = ? AND UserID = ?";

    public JdbcPostDAO() {
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
    // Get database connection
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
    public void insertPost(Post post) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     INSERT_POST_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, post.getUserId());
            preparedStatement.setString(2, post.getContentURL());
            preparedStatement.setString(3, post.getCaption());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        post.setPostId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    @Override
    public Post getPostById(int postId) throws SQLException {
        Post post = null;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POST_BY_ID)) {

            preparedStatement.setInt(1, postId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                post = extractPostFromResultSet(rs);
            }
        }

        return post;
    }

    @Override
    public List<Post> getPostsByUserId(int userId) throws SQLException {
        List<Post> posts = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POSTS_BY_USER_ID)) {

            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Post post = extractPostFromResultSet(rs);
                posts.add(post);
            }
        }

        return posts;
    }

    @Override
    public List<Post> getAllPosts(int offset, int limit) throws SQLException {
        List<Post> posts = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_POSTS)) {

            preparedStatement.setInt(1, offset);
            preparedStatement.setInt(2, limit);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Post post = extractPostFromResultSet(rs);
                posts.add(post);
            }
        }

        return posts;
    }

    @Override
    public List<Post> getFeedPostsForUser(int userId, int offset, int limit) throws SQLException {
        List<Post> posts = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FEED_POSTS)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, offset);
            preparedStatement.setInt(4, limit);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Post post = extractPostFromResultSet(rs);
                // Add this line to set the username if your Post model supports it
                post.setUsername(rs.getString("Username"));
                posts.add(post);
            }
        }

        return posts;
    }

    @Override
    public boolean updatePost(Post post) throws SQLException {
        boolean rowUpdated;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POST_SQL)) {

            preparedStatement.setString(1, post.getContentURL());
            preparedStatement.setString(2, post.getCaption());
            preparedStatement.setInt(3, post.getPostId());

            rowUpdated = preparedStatement.executeUpdate() > 0;
        }

        return rowUpdated;
    }

    @Override
    public boolean deletePost(int postId) throws SQLException {
        boolean rowDeleted;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_POST_SQL)) {

            preparedStatement.setInt(1, postId);
            rowDeleted = preparedStatement.executeUpdate() > 0;
        }

        return rowDeleted;
    }

    @Override
    public int getPostCountByUserId(int userId) throws SQLException {
        int count = 0;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_POSTS_BY_USER_ID)) {

            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        }

        return count;
    }

    @Override
    public List<Post> getPostsByHashtag(String hashtag, int offset, int limit) throws SQLException {
        List<Post> posts = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POSTS_BY_HASHTAG)) {

            preparedStatement.setString(1, hashtag);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Post post = extractPostFromResultSet(rs);
                posts.add(post);
            }
        }

        return posts;
    }

    // Helper method to extract a Post object from a ResultSet
    private Post extractPostFromResultSet(ResultSet rs) throws SQLException {
        int postId = rs.getInt("PostID");
        int userId = rs.getInt("UserID");
        String contentURL = rs.getString("ContentURL");
        String caption = rs.getString("Caption");
        Date createdAt = new Date(rs.getTimestamp("CreatedAt").getTime());
        return new Post(postId, userId, contentURL, caption, createdAt);
    }

    // Method to insert a like into the Likes table
    public void insertLike(int postId, int userId) throws SQLException {
        String insertLikeSQL = "INSERT INTO Likes (PostID, UserID) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertLikeSQL)) {
            preparedStatement.setInt(1, postId);
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            // Handle duplicate likes gracefully - this is expected if user tries to like twice
            System.err.println("User has already liked this post.");
        }
    }
    // Method to insert a comment into the Comments table
    public void insertComment(int postId, int userId, String content) throws SQLException {
        String insertCommentSQL = "INSERT INTO Comments (PostID, UserID, Content) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertCommentSQL)) {
            preparedStatement.setInt(1, postId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, content);

            preparedStatement.executeUpdate();
        }
    }

    // Method to get all comments for a post with username information
    public List<Comment> getCommentsByPostId(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COMMENTS_BY_POST_ID)) {

            preparedStatement.setInt(1, postId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int commentId = rs.getInt("CommentID");
                int userId = rs.getInt("UserID");
                String content = rs.getString("Content");
                Date createdAt = new Date(rs.getTimestamp("CreatedAt").getTime());
                String username = rs.getString("Username");

                Comment comment = new Comment(commentId, postId, userId, content, createdAt, username);
                comments.add(comment);
            }
        }

        return comments;
    }

    // Method to count likes for a post
    public int getLikeCountByPostId(int postId) throws SQLException {
        int count = 0;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_LIKES_BY_POST_ID)) {

            preparedStatement.setInt(1, postId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        }

        return count;
    }

    // Method to check if user has liked a post
    public boolean hasUserLikedPost(int postId, int userId) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USER_LIKED_POST)) {

            preparedStatement.setInt(1, postId);
            preparedStatement.setInt(2, userId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    }

    // Method to remove a like
    public void removeLike(int postId, int userId) throws SQLException {
        String removeLikeSQL = "DELETE FROM Likes WHERE PostID = ? AND UserID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(removeLikeSQL)) {
            preparedStatement.setInt(1, postId);
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();
        }
    }
}