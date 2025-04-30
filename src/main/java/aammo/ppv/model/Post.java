package aammo.ppv.model;
import java.util.Date;

public class Post {
    private int postId;
    private int userId;
    private String contentURL;
    private String caption;
    private Date createdAt;


    public Post(int postId, int userId, String contentURL, String caption, Date createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.contentURL = contentURL;
        this.caption = caption;
        this.createdAt = createdAt;
    }


    // Getters and Setters
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

    public String getContentURL() {
        return contentURL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", userId=" + userId +
                ", contentURL='" + contentURL + '\'' +
                ", caption='" + caption + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
