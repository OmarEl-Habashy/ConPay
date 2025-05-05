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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserController userController;
@Override
public void init(){
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
        request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        System.out.println("UserRegister_Servlet invoked");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {

            User user = userController.getUserByUsername(username);

            if (user != null && user.getHashedPassword().equals(password)) {
                // Login successful
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("loginTime", LocalDateTime.now());

                response.sendRedirect("profile.jsp");  // ✅ Adjust as needed
            } else {
                // Invalid credentials
                request.setAttribute("errorMessage", "Invalid username or password.");
                request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while logging in.");
            request.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(request, response);
        }
    }
}
