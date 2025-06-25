package aammo.ppv.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification {
    private int notificationId;
    private int recipientId;
    private int senderId;
    private NotificationType type;
    private int referenceId;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String senderUsername;

    public Notification(int notificationId, int recipientId, int senderId,
                        NotificationType type, int referenceId, String content,
                        boolean isRead, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.type = type;
        this.referenceId = referenceId;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Notification(int recipientId, int senderId, NotificationType type,
                        int referenceId, String content) {
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.type = type;
        this.referenceId = referenceId;
        this.content = content;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getFormattedTime() {

        return createdAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
    }
    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
}