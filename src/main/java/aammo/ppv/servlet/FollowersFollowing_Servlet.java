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
import java.util.List;

@WebServlet(urlPatterns = {"/user/followers/*", "/user/following/*"})
public class FollowersFollowing_Servlet extends HttpServlet {
    private UserController userController;

    @Override
    public void init() {
        System.out.println("FollowersFollowing_Servlet initialized");
        userController = new UserController(UserDAOFactory.getUserDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                String username = pathInfo.substring(1);
                User profileUser = userController.getUserByUsername(username);

                if (profileUser != null) {
                    request.setAttribute("profileUser", profileUser);

                    HttpSession session = request.getSession();
                    User currentUser = (User) session.getAttribute("user");

                    if (servletPath.equals("/user/followers")) {
                        List<User> followers = userController.getFollowers(profileUser.getUserId());
                        request.setAttribute("userList", followers);
                        request.setAttribute("listType", "followers");
                        request.getRequestDispatcher("/WEB-INF/view/user/followers.jsp").forward(request, response);
                    } else {
                        List<User> following = userController.getFollowing(profileUser.getUserId());
                        request.setAttribute("userList", following);
                        request.setAttribute("listType", "following");
                        request.getRequestDispatcher("/WEB-INF/view/user/following.jsp").forward(request, response);
                    }
                } else {
                    request.setAttribute("errorMessage", "User '" + username + "' not found");
                    request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request, response);
                }
            } else {
                HttpSession session = request.getSession();
                User currentUser = (User) session.getAttribute("user");

                if (currentUser != null) {
                    response.sendRedirect(request.getContextPath() + servletPath + "/" + currentUser.getUsername());
                } else {
                    response.sendRedirect(request.getContextPath() + "/login");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }
}