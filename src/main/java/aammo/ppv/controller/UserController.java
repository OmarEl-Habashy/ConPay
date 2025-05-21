package aammo.ppv.controller;

import aammo.ppv.dao.UserDAO;
import aammo.ppv.model.User;
import java.sql.SQLException;
import java.util.List;
//hello_1
public class UserController {
    private final UserDAO userDAO;

    // Constructor with explicit DAO
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Business logic methods
    public void insertUser(User user) throws SQLException {
        // Validation could be added here
        validateUserData(user);
        userDAO.insertUser(user);
    }
    public User LoginUser(String username, String password) throws SQLException {
        return userDAO.LoginUser(username, password);
    }

    public User selectUser(int userId) throws SQLException {
        return userDAO.selectUser(userId);
    }

    public List<User> selectAllUsers() throws SQLException {
        return userDAO.selectAllUsers();
    }

    public boolean updateUser(User user) throws SQLException {
        validateUserData(user);
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int id) throws SQLException {
        // Any pre-deletion business logic would go here
        return userDAO.deleteUser(id);
    }

    // Example of business logic validation
    private void validateUserData(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    public User getUserByUsername(String username) throws SQLException {
        return userDAO.getUserByUsername(username);
    }

    public int getFollowerCount(int userId) throws SQLException {
        return userDAO.getFollowerCount(userId);
    }

    public int getFollowingCount(int userId) throws SQLException {
        return userDAO.getFollowingCount(userId);
    }

    public boolean isFollowing(int followerId, int followeeId) throws SQLException {
        return userDAO.isFollowing(followerId, followeeId);
    }

    public void followUser(int followerId, int followeeId) throws SQLException {
        userDAO.followUser(followerId, followeeId);
    }

    public void unfollowUser(int followerId, int followeeId) throws SQLException {
        userDAO.unfollowUser(followerId, followeeId);
    }

    public List<User> searchUsersByUsername(String query) throws SQLException {
        return userDAO.searchUsersByUsername(query);
    }
}