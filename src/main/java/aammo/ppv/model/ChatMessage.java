package aammo.ppv.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage {
    private int messageId;
    private int roomId;
    private int senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
    private String type;

    public ChatMessage() {
    }

    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFormattedTime() {
        return (createdAt != null) ?
                createdAt.format(DateTimeFormatter.ofPattern("h:mm a")) :
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));
    }
}