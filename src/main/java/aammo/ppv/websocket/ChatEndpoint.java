//package aammo.ppv.websocket;
//
//import aammo.ppv.model.ChatMessage;
//import aammo.ppv.model.User;
//import aammo.ppv.service.ChatService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.websocket.*;
//import jakarta.websocket.server.PathParam;
//import jakarta.websocket.server.ServerEndpoint;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//@ServerEndpoint("/chat/{roomId}/{userId}/{username}")
//public class ChatEndpoint {
//
//    private static Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
//    private static Map<String, String> users = new HashMap<>();
//    private Session session;
//    private String roomId;
//    private String userId;
//    private String username;
//    private static final ChatService chatService = new ChatService();
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    @OnOpen
//    public void onOpen(Session session, @PathParam("roomId") String roomId,
//                       @PathParam("userId") String userId,
//                       @PathParam("username") String username) {
//        this.session = session;
//        this.roomId = roomId;
//        this.userId = userId;
//        this.username = username;
//        chatEndpoints.add(this);
//        users.put(session.getId(), username);
//
//        ChatMessage message = new ChatMessage();
//        message.setRoomId(Integer.parseInt(roomId));
//        message.setSenderId(Integer.parseInt(userId));
//        message.setSenderName(username);
//        message.setContent(username + " joined the chat");
//        message.setType("JOIN");
//
//        broadcast(message);
//    }
//
//    @OnMessage
//    public void onMessage(Session session, String message) throws IOException {
//        try {
//            ChatMessage chatMessage = mapper.readValue(message, ChatMessage.class);
//            chatMessage.setSenderName(username);
//
//            // Set the message type to CHAT if not specified
//            if (chatMessage.getType() == null || chatMessage.getType().isEmpty()) {
//                chatMessage.setType("CHAT");
//            }
//
//            // Save message to database if it's a chat message
//            if ("CHAT".equals(chatMessage.getType())) {
//                chatService.saveMessage(chatMessage);
//            }
//
//            broadcast(chatMessage);
//        } catch (Exception e) {
//            System.err.println("Error processing message: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @OnClose
//    public void onClose(Session session) {
//        chatEndpoints.remove(this);
//
//        ChatMessage message = new ChatMessage();
//        message.setRoomId(Integer.parseInt(roomId));
//        message.setSenderId(Integer.parseInt(userId));
//        message.setSenderName(username);
//        message.setContent(username + " left the chat");
//        message.setType("LEAVE");
//
//        broadcast(message);
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        System.err.println("Error in chat websocket: " + throwable.getMessage());
//        throwable.printStackTrace();
//    }
//
//    private void broadcast(ChatMessage message) {
//        try {
//            String messageJson = mapper.writeValueAsString(message);
//
//            for (ChatEndpoint endpoint : chatEndpoints) {
//                // Only send to endpoints in the same room
//                if (endpoint.roomId.equals(this.roomId)) {
//                    synchronized (endpoint) {
//                        try {
//                            endpoint.session.getBasicRemote().sendText(messageJson);
//                        } catch (IOException e) {
//                            chatEndpoints.remove(endpoint);
//                            try {
//                                endpoint.session.close();
//                            } catch (IOException e1) {
//                                // Ignore
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Error broadcasting message: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}