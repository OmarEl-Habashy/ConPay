package aammo.ppv.dao;

import java.sql.SQLException;
import java.util.List;

import aammo.ppv.model.Comment;
import aammo.ppv.model.Post;

public interface PostDAO {
    // Create a new post
    void insertPost(Post post) throws SQLException;

    // Get a post by its ID
    Post getPostById(int postId) throws SQLException;

    // Get all posts from a specific user
    List<Post> getPostsByUserId(int userId) throws SQLException;

    // Get all posts (potentially with pagination)
    List<Post> getAllPosts(int offset, int limit) throws SQLException;

    // Get posts from users that a specific user follows (for feed)
    List<Post> getFeedPostsForUser(int userId, int offset, int limit) throws SQLException;

    // Update a post
    boolean updatePost(Post post) throws SQLException;

    // Delete a post
    boolean deletePost(int postId) throws SQLException;

    // Get the count of posts for a specific user
    int getPostCountByUserId(int userId) throws SQLException;

    // Get posts with a specific hashtag (optional feature)
    List<Post> getPostsByHashtag(String hashtag, int offset, int limit) throws SQLException;

    // Insert a like for a post
    void insertLike(int postId, int userId) throws SQLException;

    // Remove a like from a post
    void removeLike(int postId, int userId) throws SQLException;

    // Insert a comment
    void insertComment(int postId, int userId, String content) throws SQLException;
    
    // Get all comments for a post with username information
    List<Comment> getCommentsByPostId(int postId) throws SQLException;
    
    // Count likes for a post
    int getLikeCountByPostId(int postId) throws SQLException;
    
    // Check if a user has liked a post
    boolean hasUserLikedPost(int postId, int userId) throws SQLException;
}
