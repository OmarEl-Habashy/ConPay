package aammo.ppv.dao;

import aammo.ppv.model.ChatMessage;
import aammo.ppv.model.ChatRoom;
import java.sql.SQLException;
import java.util.List;

public interface ChatDAO {
    ChatRoom createRoom(String name) throws SQLException;
    void addUserToRoom(int userId, int roomId) throws SQLException;
    void removeUserFromRoom(int userId, int roomId) throws SQLException;
    ChatMessage saveMessage(ChatMessage message) throws SQLException;
    List<ChatMessage> getRoomMessages(int roomId, int limit, int offset) throws SQLException;
    List<ChatRoom> getUserRooms(int userId) throws SQLException;
    ChatRoom getRoomById(int roomId) throws SQLException;
    boolean isUserInRoom(int userId, int roomId) throws SQLException;
}