package aammo.ppv.model;

import java.util.Date;

public class Like {
    private int likeId;
    private int postId;
    private int userId;
    private Date createdAt;


    public Like(int likeId, int postId, int userId, Date createdAt) {
        this.likeId = likeId;
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }


    // Getters and Setters
    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Like{" +
                "likeId=" + likeId +
                ", postId=" + postId +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}
