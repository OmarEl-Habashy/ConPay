package aammo.ppv.servlet;

import aammo.ppv.controller.UserController;
import aammo.ppv.dao.UserDAOFactory;
import aammo.ppv.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/register")
public class UserRegister_Servlet extends HttpServlet {
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

        if (user != null) {
            // User already logged in, redirect to feed
            response.sendRedirect(request.getContextPath() + "/feed");
            System.out.println("User already logged in, redirecting to feed");
            return;
        }

        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Forward to the registration page
        request.getRequestDispatcher("/WEB-INF/view/user/register.jsp").forward(request, response);
        System.out.println("UserRegister_Servlet invoked");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Get form parameters
            String username = request.getParameter("userName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Create user object
            User user = new User(6, username, email, password, null, LocalDateTime.now());

            // Try to register the user in the database
            userController.insertUser(user);

            // After successful registration, set user in session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            // Redirect to feed
            response.sendRedirect(request.getContextPath() + "/feed");
            System.out.println("User registered with ID: " + user.getUserId() + ". Redirecting to feed");

        } catch (SQLException e) {
            // Handle database errors
            System.err.println("Database error during registration: " + e.getMessage());
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/view/user/register.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            System.err.println("Validation error during registration: " + e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/user/register.jsp").forward(request, response);
        } catch (Exception e) {
            // Handle unexpected errors
            System.err.println("Unexpected error during registration: " + e.getMessage());
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            request.getRequestDispatcher("/WEB-INF/view/user/register.jsp").forward(request, response);
        }
    }
}