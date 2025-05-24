//package aammo.ppv.servlet;
//
//import jakarta.mail.*;
//import jakarta.mail.internet.*;
//import java.util.Properties;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.stream.Collectors;
//
//// You'll need a JSON library like JSON-P (included in Jakarta EE) or Jackson
//import jakarta.json.Json;
//import jakarta.json.JsonObject;
//import jakarta.json.JsonReader;
//
//@WebServlet("/send-email")
//public class TestMail extends HttpServlet {
//
//    private static final long serialVersionUID = 1L;
//    private static final String WEB3FORMS_API_URL = "https://api.web3forms.com/submit";
//    private static final String ACCESS_KEY = "0546d7cd-d257-4e79-920e-ac5873d74133";
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        // Get parameters from request
//        String recipientEmail = request.getParameter("email");
//        String username = request.getParameter("username");
//        String subject = request.getParameter("subject");
//        String emailType = request.getParameter("type"); // welcome or login
//
//        if (recipientEmail == null || username == null || emailType == null) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write("Missing required parameters");
//            return;
//        }
//
//        String message;
//        if ("welcome".equals(emailType)) {
//            message = createWelcomeEmail(username);
//            if (subject == null || subject.isEmpty()) {
//                subject = "Welcome to Our Platform!";
//            }
//        } else if ("login".equals(emailType)) {
//            message = createLoginNotificationEmail(username);
//            if (subject == null || subject.isEmpty()) {
//                subject = "New Login Detected";
//            }
//        } else {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write("Invalid email type");
//            return;
//        }
//
//        // Send the email
//        try {
//            String result = sendWelcomeEmail(email, username, request);
//
//            response.setContentType("application/json");
//            response.getWriter().write(result);
//
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.getWriter().write("Error sending email: " + e.getMessage());
//        }
//    }
//
//    private void sendWelcomeEmail(String email, String username, HttpServletRequest request) {
//        try {
//            URL url = new URL(request.getRequestURL().toString());
//            String baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + request.getContextPath();
//
//            URL emailUrl = new URL(baseUrl + "/send-email");
//            HttpURLConnection connection = (HttpURLConnection) emailUrl.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            connection.setDoOutput(true);
//
//            String params = "email=" + URLEncoder.encode(email, "UTF-8") +
//                    "&username=" + URLEncoder.encode(username, "UTF-8") +
//                    "&type=welcome";
//
//            try (OutputStream os = connection.getOutputStream()) {
//                os.write(params.getBytes("UTF-8"));
//            }
//
//            int responseCode = connection.getResponseCode();
//            System.out.println("Welcome email sent, response code: " + responseCode);
//        } catch (Exception e) {
//            System.err.println("Error sending welcome email: " + e.getMessage());
//        }
//    }    private String createWelcomeEmail(String username) {
//        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 5px;\">" +
//                "<h2 style=\"color: #333; border-bottom: 1px solid #eaeaea; padding-bottom: 10px;\">Welcome, " + username + "!</h2>" +
//                "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">Thank you for registering on our platform. We're excited to have you join our community!</p>" +
//                "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">You can now:</p>" +
//                "<ul style=\"color: #555; font-size: 16px; line-height: 1.5;\">" +
//                "<li>Create and share posts</li>" +
//                "<li>Connect with other users</li>" +
//                "<li>Explore trending content</li>" +
//                "</ul>" +
//                "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">If you have any questions, feel free to contact our support team.</p>" +
//                "<div style=\"margin-top: 20px; padding-top: 20px; border-top: 1px solid #eaeaea; text-align: center; color: #888; font-size: 14px;\">" +
//                "<p>This is an automated message, please do not reply to this email.</p>" +
//                "</div>" +
//                "</div>";
//    }
//
//    private String createLoginNotificationEmail(String username) {
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
//
//        String formattedDate = now.format(dateFormatter);
//        String formattedTime = now.format(timeFormatter);
//
//        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 5px;\">" +
//                "<h2 style=\"color: #333; border-bottom: 1px solid #eaeaea; padding-bottom: 10px;\">New Login Alert</h2>" +
//                "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">Hello " + username + ",</p>" +
//                "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">We detected a new login to your account:</p>" +
//                "<div style=\"background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 15px 0;\">" +
//                "<p style=\"margin: 5px 0; color: #555;\"><strong>Date:</strong> " + formattedDate + "</p>" +
//                "<p style=\"margin: 5px 0; color: #555;\"><strong>Time:</strong> " + formattedTime + "</p>" +
//                "<p style=\"margin: 5px 0; color: #555;\"><strong>Username:</strong> " + username + "</p>" +
//                "</div>" +
//                "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">If this was you, you can ignore this message.</p>" +
//                "<p style=\"color: #555; font-size: 16px; line-height: 1.5;\">If you did not log in recently, please reset your password immediately.</p>" +
//                "<div style=\"margin-top: 20px; padding-top: 20px; border-top: 1px solid #eaeaea; text-align: center; color: #888; font-size: 14px;\">" +
//                "<p>This is an automated message, please do not reply to this email.</p>" +
//                "</div>" +
//                "</div>";
//    }
//}