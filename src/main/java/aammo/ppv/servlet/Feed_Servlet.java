package aammo.ppv.servlet;
import aammo.ppv.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/feed")
public class Feed_Servlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Prevent caching to handle back button
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Retrieve the User object from the session
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            // Redirect to the registration page if no user is logged in
            response.sendRedirect(request.getContextPath() + "/register");
            System.out.println("No user in session, redirecting to register");
        } else {
            // Forward to the feed page
            request.getRequestDispatcher("/WEB-INF/view/feed.jsp").forward(request, response);
            System.out.println("Feed invoked for user: " + user.getUsername());
        }
    }
}