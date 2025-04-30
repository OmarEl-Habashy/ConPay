package aammo.ppv.model;

import java.util.Date;

public class Follow {
    private int followerId;
    private int followeeId;
    private Date createdAt;


    public Follow(int followerId, int followeeId, Date createdAt) {
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.createdAt = createdAt;
    }


    // Getters and Setters
    public int getFollowerId() {
        return followerId;
    }

    public void setFollowerId(int followerId) {
        this.followerId = followerId;
    }

    public int getFolloweeId() {
        return followeeId;
    }

    public void setFolloweeId(int followeeId) {
        this.followeeId = followeeId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public String toString() {
        return "Follow{" +
                "followerId=" + followerId +
                ", followeeId=" + followeeId +
                ", createdAt=" + createdAt +
                '}';
    }
}
