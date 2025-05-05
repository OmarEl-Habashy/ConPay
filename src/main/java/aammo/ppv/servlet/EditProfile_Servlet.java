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

@WebServlet("/user/editProfile")
public class EditProfile_Servlet extends HttpServlet {
    private UserController userController;

    @Override
    public void init(){
        userController = new UserController(UserDAOFactory.getUserDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            System.out.println("User not logged in, redirecting to login page");
            return;
        }
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/view/user/editProfile.jsp").forward(request, response);
        System.out.println("EditProfile_Servlet GET invoked");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            System.out.println("User not logged in, redirecting to login page");
            return;
        }

        String newUsername = request.getParameter("username");
        String newBio = request.getParameter("bio");

        try {
            user.setUsername(newUsername);
            user.setBio(newBio);
            userController.updateUser(user);
            session.setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/user/profile");
            System.out.println("Profile updated successfully, redirecting to profile page");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to update profile: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/user/profile.jsp").forward(request, response);
        }
    }
}
