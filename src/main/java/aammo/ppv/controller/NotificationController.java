package aammo.ppv.controller;

import aammo.ppv.dao.NotificationDAO;

import aammo.ppv.dao.UserDAOFactory;
import aammo.ppv.model.Notification;
import aammo.ppv.model.User;

import java.sql.SQLException;
import java.util.List;

public class NotificationController {
    private final NotificationDAO notificationDAO;

    public NotificationController(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public void createNotification(Notification notification) throws SQLException {
        notificationDAO.createNotification(notification);
    }

    public Notification getNotification(int notificationId) throws SQLException {
        return notificationDAO.getNotification(notificationId);
    }

    public List<Notification> getUserNotifications(int userId) throws SQLException {
        List<Notification> notifications = notificationDAO.getUserNotifications(userId);

        UserController userController = new UserController(UserDAOFactory.getUserDAO());

        for (Notification notification : notifications) {
            try {
                User sender = userController.selectUser(notification.getSenderId());
                if (sender != null) {
                    notification.setSenderUsername(sender.getUsername());
                } else {
                    notification.setSenderUsername("User_" + notification.getSenderId());
                }
            } catch (SQLException e) {
                notification.setSenderUsername("User_" + notification.getSenderId());
                System.err.println("Error fetching username for user ID " + notification.getSenderId() + ": " + e.getMessage());
                System.err.println("Error fetching sender: " + e.getMessage());
            }
        }

        return notifications;
    }

    public List<Notification> getUnreadNotifications(int userId) throws SQLException {
        return notificationDAO.getUnreadNotifications(userId);
    }

    public boolean markAsRead(int notificationId) throws SQLException {
        return notificationDAO.markAsRead(notificationId);
    }

    public boolean markAllAsRead(int userId) throws SQLException {
        return notificationDAO.markAllAsRead(userId);
    }

    public boolean deleteNotification(int notificationId) throws SQLException {
        return notificationDAO.deleteNotification(notificationId);
    }
}