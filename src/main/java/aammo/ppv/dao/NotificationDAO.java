package aammo.ppv.dao;

import aammo.ppv.model.Notification;
import java.sql.SQLException;
import java.util.List;

public interface NotificationDAO {
    void createNotification(Notification notification) throws SQLException;
    Notification getNotification(int notificationId) throws SQLException;
    List<Notification> getUserNotifications(int userId) throws SQLException;
    List<Notification> getUnreadNotifications(int userId) throws SQLException;
    boolean markAsRead(int notificationId) throws SQLException;
    boolean markAllAsRead(int userId) throws SQLException;
    boolean deleteNotification(int notificationId) throws SQLException;
}