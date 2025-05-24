package aammo.ppv.dao;

public class NotificationDAOFactory {
    private static NotificationDAO notificationDAO;

    public static NotificationDAO getNotificationDAO() {
        if (notificationDAO == null) {
            notificationDAO = new JdbcNotificationDAO();
        }
        return notificationDAO;
    }
}