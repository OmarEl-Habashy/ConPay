package aammo.ppv.controller;

import aammo.ppv.dao.PostDAO;
import aammo.ppv.model.Post;
import java.sql.SQLException;
import java.util.List;

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
}