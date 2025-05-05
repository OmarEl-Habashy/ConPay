package aammo.ppv.dao;

import aammo.ppv.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    // Basic CRUD operations
    void insertUser(User user) throws SQLException;
    User selectUser(int userId) throws SQLException;
    boolean LoginUser(User user) throws SQLException;
    List<User> selectAllUsers() throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean deleteUser(int id) throws SQLException;

    // Additional methods for social media functionality
    User getUserByUsername(String username) throws SQLException;
    int getFollowerCount(int userId) throws SQLException;
    int getFollowingCount(int userId) throws SQLException;
    boolean isFollowing(int followerId, int followeeId) throws SQLException;
    void followUser(int followerId, int followeeId) throws SQLException;
    void unfollowUser(int followerId, int followeeId) throws SQLException;
    List<User> getFollowers(int userId) throws SQLException;
    List<User> getFollowing(int userId) throws SQLException;
}