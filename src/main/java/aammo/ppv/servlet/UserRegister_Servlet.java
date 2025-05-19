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
        userController = new UserController(UserDAOFactory.getUserDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            response.sendRedirect(request.getContextPath() + "/feed");
            System.out.println("User already logged in, redirecting to feed");
            return;
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        request.getRequestDispatcher("/WEB-INF/view/user/register.jsp").forward(request, response);
        System.out.println("UserRegister_Servlet invoked");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String username = request.getParameter("userName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Server-side validation
            if (username == null || username.trim().length() < 3) {
                throw new IllegalArgumentException("Username must be at least 3 characters.");
            }

            if (email == null || !email.endsWith("@gmail.com")) {
                throw new IllegalArgumentException("Email must be a valid @gmail.com address.");
            }

            if (password == null || password.length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters.");
            }

            // Create user
            User user = new User(6, username, email, password, null, LocalDateTime.now());

            // Insert into DB
            userController.insertUser(user);

            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            response.sendRedirect(request.getContextPath() + "/feed");
            System.out.println("User registered with ID: " + user.getUserId() + ". Redirecting to feed");

        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/view/user/register.jsp").include(request, response);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/user/register.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Unexpected error during registration: " + e.getMessage());
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            request.getRequestDispatcher("/WEB-INF/view/user/register.jsp").include(request, response);
        }
    }
}
