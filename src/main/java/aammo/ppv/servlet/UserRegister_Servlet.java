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
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
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

            if (email == null || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                throw new IllegalArgumentException("Email must be a valid address.");
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

            // Send welcome email asynchronously
            new Thread(() -> {
                try {
                    // Create and configure email properties
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.port", "587");
                    props.put("mail.smtp.ssl.trust", "*");

                    // Create mail session
                    Session mailSession = Session.getInstance(props, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("omar.walid.gad.0678@gmail.com", "cilr illp bmxs tasm");
                        }
                    });

                    // Create email message
                    Message message = new MimeMessage(mailSession);
                    message.setFrom(new InternetAddress("omar.walid.gad.0678@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                    message.setSubject("Welcome to Our Platform!");

                    // Create HTML content for welcome email
                    String htmlContent =
                            "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 5px;\">" +
                                    "<h2 style=\"color: #333; border-bottom: 1px solid #eaeaea; padding-bottom: 10px;\">Welcome to Our Platform!</h2>" +
                                    "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">Hello " + username + ",</p>" +
                                    "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">Thank you for creating an account with us. We're excited to have you join our community!</p>" +
                                    "<div style=\"background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 15px 0;\">" +
                                    "<p style=\"margin: 5px 0; color: #555;\"><strong>Username:</strong> " + username + "</p>" +
                                    "<p style=\"margin: 5px 0; color: #555;\"><strong>Email:</strong> " + email + "</p>" +
                                    "</div>" +
                                    "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">Here are some things you can do now:</p>" +
                                    "<ul style=\"color: #555; font-size: 16px; line-height: 1.5;\">" +
                                    "<li>Complete your profile</li>" +
                                    "<li>Explore our features</li>" +
                                    "<li>Connect with other users</li>" +
                                    "</ul>" +
                                    "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">We hope you enjoy your experience!</p>" +
                                    "<div style=\"margin-top: 20px; padding-top: 20px; border-top: 1px solid #eaeaea; text-align: center; color: #888; font-size: 14px;\">" +
                                    "<p>This is an automated message, please do not reply to this email.</p>" +
                                    "</div>" +
                                    "</div>";

                    // Set HTML content
                    message.setContent(htmlContent, "text/html; charset=utf-8");

                    // Send email
                    Transport.send(message);
                    LogManager.logAction("REGISTER_EMAIL_SENT", "User=" + username + ", Email=" + email);

                } catch (Exception e) {
                    LogManager.logAction("REGISTER_EMAIL_FAILED", "User=" + username + ", Error=" + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

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
