package aammo.ppv.controller;

import aammo.ppv.dao.UserDAO;
import aammo.ppv.model.User;
import aammo.ppv.service.NotificationService;
import java.sql.SQLException;
import java.util.List;

public class UserController {
    private final UserDAO userDAO;
    private NotificationService notificationService;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.notificationService = null;
    }

    private NotificationService getNotificationService() {
        if (this.notificationService == null) {
            this.notificationService = NotificationService.getInstance();
        }
        return this.notificationService;
    }

    public void insertUser(User user) throws SQLException {
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
        getNotificationService().notifyFollowAction(followerId, followeeId);
    }

    public void unfollowUser(int followerId, int followeeId) throws SQLException {
        userDAO.unfollowUser(followerId, followeeId);
        getNotificationService().notifyUnfollowAction(followerId, followeeId);
    }

    public List<User> searchUsersByUsername(String query) throws SQLException {
        return userDAO.searchUsersByUsername(query);
    }

    public String getUserEmail(String username) throws SQLException {
        return userDAO.getUserEmail(username);
    }
    public List<User> getFollowers(int userId) throws SQLException {
        return userDAO.getFollowers(userId);
    }

    public List<User> getFollowing(int userId) throws SQLException {
        return userDAO.getFollowing(userId);
    }
}