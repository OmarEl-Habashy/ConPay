package aammo.ppv.observer;

import aammo.ppv.controller.NotificationController;
import aammo.ppv.controller.UserController;
import aammo.ppv.dao.NotificationDAOFactory;
import aammo.ppv.dao.UserDAOFactory;
import aammo.ppv.model.Notification;
import aammo.ppv.model.NotificationType;
import aammo.ppv.model.User;
import aammo.ppv.service.LogManager;

import java.sql.SQLException;

public class NotificationObserver implements Observer {
    private final NotificationController notificationController;
    private final UserController userController;

    public NotificationObserver() {
        this.notificationController = new NotificationController(NotificationDAOFactory.getNotificationDAO());
        this.userController = new UserController(UserDAOFactory.getUserDAO());
    }

    @Override
    public void update(String action, int senderId, int recipientId, int referenceId) {
        Thread notificationThread = new Thread(() -> {
            try {
                if (senderId == recipientId) {
                    return;
                }

                User sender = userController.selectUser(senderId);
                String senderUsername = (sender != null) ? sender.getUsername() : "User_" + senderId;

                if ("FOLLOW".equals(action)) {
                    Notification notification = new Notification(
                            recipientId, senderId, NotificationType.FOLLOW,
                            referenceId, senderUsername + " started following you");
                    notificationController.createNotification(notification);
                    LogManager.logAction("NOTIFICATION_CREATED", "Type=FOLLOW, Recipient=" + recipientId + ", Sender=" + senderId);
                } else if ("UNFOLLOW".equals(action)) {
                    Notification notification = new Notification(
                            recipientId, senderId, NotificationType.UNFOLLOW,
                            referenceId, senderUsername + " stopped following you");
                    notificationController.createNotification(notification);
                    LogManager.logAction("NOTIFICATION_CREATED", "Type=UNFOLLOW, Recipient=" + recipientId + ", Sender=" + senderId);
                } else if ("LIKE".equals(action)) {
                    Notification notification = new Notification(
                            recipientId, senderId, NotificationType.LIKE,
                            referenceId, senderUsername + " liked your post");
                    notificationController.createNotification(notification);
                    LogManager.logAction("NOTIFICATION_CREATED", "Type=LIKE, Recipient=" + recipientId + ", Sender=" + senderId + ", PostId=" + referenceId);
                } else if ("COMMENT".equals(action)) {
                    Notification notification = new Notification(
                            recipientId, senderId, NotificationType.COMMENT,
                            referenceId, senderUsername + " commented on your post");
                    notificationController.createNotification(notification);
                    LogManager.logAction("NOTIFICATION_CREATED", "Type=COMMENT, Recipient=" + recipientId + ", Sender=" + senderId);
                } else if ("MENTION".equals(action)) {
                    Notification notification = new Notification(
                            recipientId, senderId, NotificationType.MENTION,
                            referenceId, senderUsername + " mentioned you in a post");
                    notificationController.createNotification(notification);
                    LogManager.logAction("NOTIFICATION_CREATED", "Type=MENTION, Recipient=" + recipientId + ", Sender=" + senderId);
                }
            } catch (SQLException e) {
                LogManager.logAction("NOTIFICATION_ERROR",
                        "Failed to create notification: " + e.getMessage() + " | Action: " + action +
                                ", SenderId: " + senderId + ", RecipientId: " + recipientId);
                e.printStackTrace();
            }
        });

        notificationThread.start();
    }}