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
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println("Cookie Name: " + cookie.getName());
                System.out.println("Cookie Value: " + cookie.getValue());
            }
        }

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
                    userCookie.setMaxAge(60 * 60 * 24 * 7);
                    userCookie.setPath("/");
                    userCookie.setHttpOnly(true);
                     userCookie.setSecure(true);

                    response.setHeader("Set-Cookie",
                            String.format("username=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax",
                                    user.getUsername(), 60 * 60 * 24 * 7)
                    );

                    response.addCookie(userCookie);
                } else {
                    Cookie userCookie = new Cookie("username", "");
                    userCookie.setMaxAge(0);
                    userCookie.setPath("/");
                    response.addCookie(userCookie);
                }

                new Thread(() -> {
                    try {
                        Properties props = new Properties();
                        props.put("mail.smtp.auth", "true");
                        props.put("mail.smtp.starttls.enable", "true");
                        props.put("mail.smtp.host", "smtp.gmail.com");
                        props.put("mail.smtp.port", "587");
                        props.put("mail.smtp.ssl.trust", "*");

                        Session mailSession = Session.getInstance(props, new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("omar.walid.gad.0678@gmail.com", "cilr illp bmxs tasm");
                            }
                        });

                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

                        String formattedDate = now.format(dateFormatter);
                        String formattedTime = now.format(timeFormatter);

                        Message message = new MimeMessage(mailSession);
                        message.setFrom(new InternetAddress("omar.walid.gad.0678@gmail.com"));
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
                        message.setSubject("New Login to Your Account");

                        String htmlContent =
                                "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 5px;\">" +
                                        "<h2 style=\"color: #333; border-bottom: 1px solid #eaeaea; padding-bottom: 10px;\">New Login Alert</h2>" +
                                        "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">Hello " + user.getUsername() + ",</p>" +
                                        "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">We detected a new login to your account:</p>" +
                                        "<div style=\"background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 15px 0;\">" +
                                        "<p style=\"margin: 5px 0; color: #555;\"><strong>Date:</strong> " + formattedDate + "</p>" +
                                        "<p style=\"margin: 5px 0; color: #555;\"><strong>Time:</strong> " + formattedTime + "</p>" +
                                        "<p style=\"margin: 5px 0; color: #555;\"><strong>Username:</strong> " + user.getUsername() + "</p>" +
                                        "</div>" +
                                        "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">If this was you, you can ignore this message.</p>" +
                                        "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">If you did not log in recently, please reset your password immediately.</p>" +
                                        "<div style=\"margin-top: 20px; padding-top: 20px; border-top: 1px solid #eaeaea; text-align: center; color: #888; font-size: 14px;\">" +
                                        "<p>This is an automated message, please do not reply to this email.</p>" +
                                        "</div>" +
                                        "</div>";

                        message.setContent(htmlContent, "text/html; charset=utf-8");

                        Transport.send(message);
                        LogManager.logAction("LOGIN_EMAIL_SENT", "User=" + user.getUsername() + ", Email=" + user.getEmail());

                    } catch (Exception e) {
                        LogManager.logAction("LOGIN_EMAIL_FAILED", "User=" + user.getUsername() + ", Error=" + e.getMessage());
                        e.printStackTrace();
                    }
                }).start();

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