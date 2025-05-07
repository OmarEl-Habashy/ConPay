package aammo.ppv.servlet;

import aammo.ppv.controller.UserController;
import aammo.ppv.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import aammo.ppv.dao.UserDAOFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/search")
public class Search_Servlet extends HttpServlet {

    private UserController userController;

    @Override
    public void init() throws ServletException {
        super.init();
        userController = new UserController(UserDAOFactory.getUserDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchQuery = request.getParameter("query");

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            User searchedUser = userController.getUserByUsername(searchQuery.trim());
            if (searchedUser != null) {
                request.setAttribute("profileUser", searchedUser);
                request.setAttribute("followerCount", userController.getFollowerCount(searchedUser.getUserId()));
                request.setAttribute("followingCount", userController.getFollowingCount(searchedUser.getUserId()));
                request.setAttribute("isOwnProfile", false); // Adjust based on session user
                request.getRequestDispatcher("/WEB-INF/view/user/profile.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "User not found.");
                request.getRequestDispatcher("/WEB-INF/view/home.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error while retrieving user profile", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchQuery = request.getParameter("query");

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            User user = userController.getUserByUsername(searchQuery.trim());
            if (user != null) {
                response.sendRedirect(request.getContextPath() + "/search?query=" + user.getUsername());
            } else {
                request.setAttribute("error", "User not found.");
                request.getRequestDispatcher("/WEB-INF/view/home.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error while searching for user", e);
        }
    }
}