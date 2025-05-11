package aammo.ppv.controller;

import java.sql.SQLException;
import java.util.List;

import aammo.ppv.dao.PostDAO;
import aammo.ppv.model.Comment;
import aammo.ppv.model.Post;

public class PostController {
    private final PostDAO postDAO;

    public PostController(PostDAO postDAO) {
        this.postDAO = postDAO;
    }

    public void createPost(Post post) throws SQLException {
        // Validate post data
        validatePostData(post);

        // Insert the post
        postDAO.insertPost(post);
    }

    public Post getPost(int postId) throws SQLException {
        return postDAO.getPostById(postId);
    }

    public List<Post> getPostsByUserId(int userId) throws SQLException {
        return postDAO.getPostsByUserId(userId);
    }

    public List<Post> getFeedPosts(int userId, int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        return postDAO.getFeedPostsForUser(userId, offset, pageSize);
    }

    public List<Post> getRecentPosts(int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        return postDAO.getAllPosts(offset, pageSize);
    }

    public boolean updatePost(Post post, int userId) throws SQLException {
        Post existingPost = postDAO.getPostById(post.getPostId());
        if (existingPost == null || existingPost.getUserId() != userId) {
            return false;
        }
        validatePostData(post);
        return postDAO.updatePost(post);
    }

    public boolean deletePost(int postId, int userId) throws SQLException {
        Post existingPost = postDAO.getPostById(postId);
        if (existingPost == null || existingPost.getUserId() != userId) {
            return false;
        }
        return postDAO.deletePost(postId);
    }

    public int getPostCount(int userId) throws SQLException {
        return postDAO.getPostCountByUserId(userId);
    }

    public List<Post> searchPostsByHashtag(String hashtag, int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        return postDAO.getPostsByHashtag(hashtag, offset, pageSize);
    }

    // Validation logic
    private void validatePostData(Post post) {
        if (post.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (post.getCaption() != null && post.getCaption().length() > 280) {
            throw new IllegalArgumentException("Caption cannot exceed 280 characters");
        }

        // You can add more validation rules as needed
    }

    // insert like
    public void insertLike(int postId, int userId) throws SQLException {
        // Validate input
        if (postId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid post ID or user ID");
        }

        // Insert like
        postDAO.insertLike(postId, userId);
    }

    // insert comment
    public void insertComment(int postId, int userId, String content) throws SQLException {
        // Validate input
        if (postId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid post ID or user ID");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        // Insert comment
        postDAO.insertComment(postId, userId, content.trim());
    }

    //get post by id
    public Post getPostById(int postId) throws SQLException {
        // Validate input
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        // Retrieve the post
        return postDAO.getPostById(postId);
    }

    // Get comments for a post
    public List<Comment> getCommentsForPost(int postId) throws SQLException {
        // Validate input
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        // Retrieve comments with username information
        return postDAO.getCommentsByPostId(postId);
    }

    // Get like count for a post
    public int getLikeCount(int postId) throws SQLException {
        // Validate input
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        // Get like count
        return postDAO.getLikeCountByPostId(postId);
    }

    // Check if a user has liked a post
    public boolean hasUserLikedPost(int postId, int userId) throws SQLException {
        // Validate input
        if (postId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid post ID or user ID");
        }

        // Check if user has liked this post
        return postDAO.hasUserLikedPost(postId, userId);
    }

// Updated toggleLike method:

    public void toggleLike(int postId, int userId) throws SQLException {
        // Validate input
        if (postId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid post ID or user ID");
        }

        // Get the post to verify it exists
        Post post = postDAO.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }

        // Allow self-likes: No check whether post.getUserId() == userId

        // Check if user has already liked this post
        if (postDAO.hasUserLikedPost(postId, userId)) {
            // If already liked, remove the like
            postDAO.removeLike(postId, userId);
        } else {
            // If not liked, add a like
            postDAO.insertLike(postId, userId);
        }
    }
}