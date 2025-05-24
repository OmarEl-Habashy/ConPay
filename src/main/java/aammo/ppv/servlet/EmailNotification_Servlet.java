package aammo.ppv.servlet;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import aammo.ppv.model.User;
import aammo.ppv.service.LogManager;
import aammo.ppv.service.TrustAllCerts;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@WebServlet("/send-login-notification")
public class EmailNotification_Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String fromEmail;
    private String fromPassword;

    @Override
    public void init() throws ServletException {
        Properties emailProps = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                LogManager.logAction("EMAIL_CONFIG", "Could not find email.properties file");
                throw new ServletException("Could not find email.properties file");
            }
            emailProps.load(input);
            fromEmail = emailProps.getProperty("mail.smtp.username");
            fromPassword = emailProps.getProperty("mail.smtp.password");
        } catch (IOException e) {
            LogManager.logAction("EMAIL_CONFIG", "Error loading email configuration: " + e.getMessage());
            throw new ServletException("Error loading email configuration", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // This endpoint shouldn't be directly accessible
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = (User) session.getAttribute("user");
        String userEmail = user.getEmail();
        String username = user.getUsername();

        // Send email asynchronously
        new Thread(() -> {
            try {
                sendLoginNotificationEmail(userEmail, username);
                LogManager.logAction("EMAIL_NOTIFICATION", "User=" + username + ", Status=SUCCESS");
            } catch (Exception e) {
                LogManager.logAction("EMAIL_NOTIFICATION", "User=" + username + ", Status=FAILED, Error=" + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        // Return success response
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":\"success\",\"message\":\"Login notification email is being sent\"}");
    }

    private void sendLoginNotificationEmail(String toEmail, String userName) {
        try {
            // Enable TrustAllCerts if needed (not recommended for production)
            TrustAllCerts.trust();

            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
                if (input != null) {
                    props.load(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Set additional properties
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "*");

            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, fromPassword);
                }
            });

            // Rest of your email sending code remains the same
            // ...

            // Get current time information
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

            String formattedDate = now.format(dateFormatter);
            String formattedTime = now.format(timeFormatter);

            // Create message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("New Login to Your Account");

            // Create HTML content
            String htmlContent =
                    "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 5px;\">" +
                            "<h2 style=\"color: #333; border-bottom: 1px solid #eaeaea; padding-bottom: 10px;\">New Login Alert</h2>" +
                            "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">Hello " + userName + ",</p>" +
                            "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">We detected a new login to your account:</p>" +
                            "<div style=\"background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 15px 0;\">" +
                            "<p style=\"margin: 5px 0; color: #555;\"><strong>Date:</strong> " + formattedDate + "</p>" +
                            "<p style=\"margin: 5px 0; color: #555;\"><strong>Time:</strong> " + formattedTime + "</p>" +
                            "<p style=\"margin: 5px 0; color: #555;\"><strong>Username:</strong> " + userName + "</p>" +
                            "</div>" +
                            "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">If this was you, you can ignore this message.</p>" +
                            "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">If you did not log in recently, please reset your password immediately.</p>" +
                            "<div style=\"margin-top: 20px; padding-top: 20px; border-top: 1px solid #eaeaea; text-align: center; color: #888; font-size: 14px;\">" +
                            "<p>This is an automated message, please do not reply to this email.</p>" +
                            "</div>" +
                            "</div>";

            // Set content
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Send message
            Transport.send(message);
            System.out.println("✅ Login notification email sent successfully to " + toEmail);

        } catch (MessagingException e) {
            System.out.println("❌ Failed to send login notification email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}