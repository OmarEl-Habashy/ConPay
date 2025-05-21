package aammo.ppv.servlet;

import aammo.ppv.controller.UserController;
import aammo.ppv.dao.UserDAOFactory;
import aammo.ppv.model.User;
import aammo.ppv.service.LogManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
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
        // Examine cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println("Cookie Name: " + cookie.getName());
                System.out.println("Cookie Value: " + cookie.getValue());
            }
        }

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
            String rememberMe = request.getParameter("rememberMe");

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Username and password are required");
            }

            User user = userController.LoginUser(username, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                if ("on".equals(rememberMe)) {
                    Cookie userCookie = new Cookie("username", user.getUsername());
                    userCookie.setMaxAge(60 * 60 * 24 * 7); // 7 days
                    userCookie.setPath("/");
                    userCookie.setHttpOnly(true);
                    // userCookie.setSecure(true); // Uncomment if using HTTPS

                    // Set SameSite attribute (Servlet API 6.0+ or via header)
                    response.setHeader("Set-Cookie",
                            String.format("username=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax",
                                    user.getUsername(), 60 * 60 * 24 * 7)
                    );

                    response.addCookie(userCookie);
                } else {
                    // Remove the cookie if it exists
                    Cookie userCookie = new Cookie("username", "");
                    userCookie.setMaxAge(0); // Delete cookie
                    userCookie.setPath("/");
                    response.addCookie(userCookie);
                }

                response.sendRedirect(request.getContextPath() + "/feed");
                System.out.println("User logged in with ID: " + user.getUserId() + ". Redirecting to feed");
            } else {
                request.setAttribute("errorMessage", "Invalid username or password");
                request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").include(request, response);
                System.out.println("Login failed: invalid credentials");
            }

        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            request.setAttribute("errorMessage", "Login failed due to a system error. Please try again.");
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error during login: " + e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        }
    }
}