package aammo.ppv.servlet;

import aammo.ppv.controller.UserController;
import aammo.ppv.dao.UserDAOFactory;
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

@WebServlet("/login")
public class UserLogin_Servlet extends HttpServlet {
    private UserController userController;

    @Override
    public void init() {
        // Initialize the UserController with UserDAO
        userController = new UserController(UserDAOFactory.getUserDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        LogManager.logAction("LOGIN_ATTEMPT", "User=" + (user != null ? user.getUsername() : "Guest") + ", Action=GET");
        if (user != null) {
            response.sendRedirect(request.getContextPath() + "/feed");
            System.out.println("User already logged in, redirecting to feed");
            return;
        }

        // Prevent caching to ensure login page isn't accessible with back button after login
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        System.out.println("UserLogin_Servlet GET invoked");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Username and password are required");
            }

            User user = userController.LoginUser(username, password);

            if (user != null) {
                // Login successful - set user in session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                response.sendRedirect(request.getContextPath() + "/feed");
                System.out.println("User logged in with ID: " + user.getUserId() + ". Redirecting to feed");
            } else {
                // Login failed - invalid credentials
                request.setAttribute("errorMessage", "Invalid username or password");
                request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").include(request, response);
                System.out.println("Login failed: invalid credentials");
            }

        } catch (SQLException e) {
            // Handle database errors
            System.err.println("Database error during login: " + e.getMessage());
            request.setAttribute("errorMessage", "Login failed due to a system error. Please try again.");
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            System.err.println("Validation error during login: " + e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        } catch (Exception e) {
            // Handle unexpected errors
            System.err.println("Unexpected error during login: " + e.getMessage());
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        }
    }
}