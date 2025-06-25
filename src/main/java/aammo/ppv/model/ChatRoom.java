package aammo.ppv.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatRoom {
    private int roomId;
    private String name;
    private LocalDateTime createdAt;

    public ChatRoom() {
    }

    public ChatRoom(int roomId, String name, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.name = name;
        this.createdAt = createdAt;
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFormattedCreatedAt() {
        return createdAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"));
    }
}