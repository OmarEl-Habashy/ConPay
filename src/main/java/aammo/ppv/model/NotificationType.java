package aammo.ppv.model;

public enum NotificationType {
    FOLLOW("started following you"),
    UNFOLLOW("unfollowed you"),
    LIKE("liked your post"),
    COMMENT("commented on your post"),
    MENTION("mentioned you in a post");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}