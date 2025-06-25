package aammo.ppv.service;

import aammo.ppv.dao.ChatDAO;
import aammo.ppv.dao.JdbcChatDAO;
import aammo.ppv.model.ChatMessage;
import aammo.ppv.model.ChatRoom;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatService {
    private final ChatDAO chatDAO;

    public ChatService() {
        this.chatDAO = new JdbcChatDAO();
    }

    public ChatRoom createRoom(String name) {
        try {
            return chatDAO.createRoom(name);
        } catch (SQLException e) {
            System.err.println("Error creating chat room: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean addUserToRoom(int userId, int roomId) {
        try {
            chatDAO.addUserToRoom(userId, roomId);
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding user to room: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public ChatMessage saveMessage(ChatMessage message) {
        try {
            return chatDAO.saveMessage(message);
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<ChatMessage> getRoomMessages(int roomId, int limit, int offset) {
        try {
            return chatDAO.getRoomMessages(roomId, limit, offset);
        } catch (SQLException e) {
            System.err.println("Error getting room messages: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ChatRoom> getUserRooms(int userId) {
        try {
            return chatDAO.getUserRooms(userId);
        } catch (SQLException e) {
            System.err.println("Error getting user rooms: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ChatRoom getRoomById(int roomId) {
        try {
            return chatDAO.getRoomById(roomId);
        } catch (SQLException e) {
            System.err.println("Error getting room by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean isUserInRoom(int userId, int roomId) {
        try {
            return chatDAO.isUserInRoom(userId, roomId);
        } catch (SQLException e) {
            System.err.println("Error checking if user is in room: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}