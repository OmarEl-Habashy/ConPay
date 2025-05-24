package aammo.ppv.servlet;

import aammo.ppv.controller.NotificationController;
import aammo.ppv.dao.NotificationDAOFactory;
import aammo.ppv.model.Notification;
import aammo.ppv.model.User;
import aammo.ppv.service.LogManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/notifications")
public class Notification_Servlet extends HttpServlet {
    private NotificationController notificationController;

    @Override
    public void init() throws ServletException {
        notificationController = new NotificationController(NotificationDAOFactory.getNotificationDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getUserId();

        String action = request.getParameter("action");
        if (action != null) {
            try {
                if ("markRead".equals(action)) {
                    String notificationId = request.getParameter("id");
                    if (notificationId != null && !notificationId.isEmpty()) {
                        notificationController.markAsRead(Integer.parseInt(notificationId));
                        LogManager.logAction("NOTIFICATION_MARKED_READ", "User=" + userId +
                                ", NotificationId=" + notificationId);
                    }
                } else if ("markAllRead".equals(action)) {
                    notificationController.markAllAsRead(userId);
                    LogManager.logAction("ALL_NOTIFICATIONS_MARKED_READ", "User=" + userId);
                }
            } catch (SQLException e) {
                LogManager.logAction("NOTIFICATION_ERROR", "Failed to update notification status: " +
                        e.getMessage());
            }
        }

        try {
            List<Notification> notifications = notificationController.getUserNotifications(userId);
            request.setAttribute("notifications", notifications);

            List<Notification> unreadNotifications = notificationController.getUnreadNotifications(userId);
            request.setAttribute("unreadNotifications", unreadNotifications);

            request.getRequestDispatcher("/WEB-INF/view/common/Notification.jsp").forward(request, response);

        } catch (SQLException e) {
            LogManager.logAction("NOTIFICATION_ERROR", "Failed to fetch notifications: " + e.getMessage());
            request.setAttribute("errorMessage", "Failed to load notifications. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request, response);
        }
    }
}