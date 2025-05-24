package aammo.ppv.controller;

import java.sql.SQLException;
import java.util.List;

import aammo.ppv.observer.UserActionSubject;
import aammo.ppv.service.NotificationService;
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

    public List<Post> getFeedPosts(int userId) throws SQLException {
        return postDAO.getFeedPostsForUser(userId, 0, 100); // 100 is just an example
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

    private void validatePostData(Post post) {
        if (post.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (post.getCaption() != null && post.getCaption().length() > 280) {
            throw new IllegalArgumentException("Caption cannot exceed 280 characters");
        }

    }

    public void insertLike(int postId, int userId) throws SQLException {
        if (postId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid post ID or user ID");
        }

        postDAO.insertLike(postId, userId);
    }

    public void insertComment(int postId, int userId, String content) throws SQLException {
        if (postId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid post ID or user ID");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        postDAO.insertComment(postId, userId, content.trim());
    }

    public Post getPostById(int postId) throws SQLException {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        return postDAO.getPostById(postId);
    }

    public List<Comment> getCommentsForPost(int postId) throws SQLException {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        return postDAO.getCommentsByPostId(postId);
    }

    public int getLikeCount(int postId) throws SQLException {
        if (postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }

        return postDAO.getLikeCountByPostId(postId);
    }

    public boolean hasUserLikedPost(int postId, int userId) throws SQLException {
        if (postId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid post ID or user ID");
        }

        return postDAO.hasUserLikedPost(postId, userId);
    }


    public void toggleLike(int postId, int userId) throws SQLException {
        if (postId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid post ID or user ID");
        }

        Post post = postDAO.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        boolean alreadyLiked = postDAO.hasUserLikedPost(postId, userId);
        if (alreadyLiked) {
            postDAO.removeLike(postId, userId);
        } else {
            postDAO.insertLike(postId, userId);
            int postOwnerId = post.getUserId();
            if (userId != postOwnerId) {
                UserActionSubject userActionSubject = UserActionSubject.getInstance();
                userActionSubject.notifyObservers("LIKE", userId, postOwnerId, postId);
            }
        }
    }
    public List<Post> searchPostsByCaption(String query) throws SQLException {
        return postDAO.searchPostsByCaption(query);
    }


}