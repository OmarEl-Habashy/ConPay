package aammo.ppv.servlet;

import aammo.ppv.controller.UserController;
import aammo.ppv.controller.PostController;
import aammo.ppv.dao.UserDAOFactory;
import aammo.ppv.dao.PostDAOFactory;
import aammo.ppv.model.User;
import aammo.ppv.model.Post;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/user/profile/*")
public class Profile_Servlet extends HttpServlet {
    private UserController userController;
    private PostController postController;

    @Override
    public void init() {
        System.out.println("Profile_Servlet initialized with mapping: " + getServletConfig().getServletName());
        userController = new UserController(UserDAOFactory.getUserDAO());
        postController = new PostController(PostDAOFactory.getPostDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            // Path format: /user/profile/{username}
            if (pathInfo != null && pathInfo.length() > 1) {
                String username = pathInfo.substring(1); // Remove leading slash
                displayUserProfile(username, request, response);
            } else {
                // If no username specified, show current user's profile
                HttpSession session = request.getSession();
                User currentUser = (User) session.getAttribute("user");

                if (currentUser != null) {
                    response.sendRedirect(request.getContextPath() + "/user/profile/" + currentUser.getUsername());
                } else {
                    // Not logged in, redirect to login
                    response.sendRedirect(request.getContextPath() + "/login");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    private void displayUserProfile(String username, HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        // Find user by username instead of ID
        User profileUser = userController.getUserByUsername(username);

        if (profileUser != null) {
            // Get current user from session to check if viewing own profile
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("user");
            boolean isOwnProfile = (currentUser != null && currentUser.getUserId() == profileUser.getUserId());

            // Get user's posts
            List<Post> userPosts = postController.getPostsByUserId(profileUser.getUserId());

            // Get follower count
            int followerCount = userController.getFollowerCount(profileUser.getUserId());

            // Get following count
            int followingCount = userController.getFollowingCount(profileUser.getUserId());

            // Check if current user follows this profile
            boolean isFollowing = false;
            if (currentUser != null) {
                isFollowing = userController.isFollowing(currentUser.getUserId(), profileUser.getUserId());
            }

            // Set attributes for the view
            request.setAttribute("profileUser", profileUser);
            request.setAttribute("isOwnProfile", isOwnProfile);
            request.setAttribute("userPosts", userPosts);
            request.setAttribute("followerCount", followerCount);
            request.setAttribute("followingCount", followingCount);
            request.setAttribute("isFollowing", isFollowing);

            // Forward to the profile JSP
            request.getRequestDispatcher("/WEB-INF/view/user/profile.jsp").forward(request, response);
        } else {
            // User not found
            request.setAttribute("errorMessage", "User '" + username + "' not found");
            request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            switch (action) {
                case "follow":
                    followUser(request, response, currentUser);
                    break;
                case "unfollow":
                    unfollowUser(request, response, currentUser);
                    break;
                case "updateProfile":
                    updateProfile(request, response, currentUser);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/user/profile/" + currentUser.getUsername());
            }
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    private void followUser(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws SQLException, IOException {
        int userToFollowId = Integer.parseInt(request.getParameter("userId"));

        userController.followUser(currentUser.getUserId(), userToFollowId);

        // Redirect back to the profile page
        String username = request.getParameter("username");
        response.sendRedirect(request.getContextPath() + "/user/profile/" + username);
    }

    private void unfollowUser(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws SQLException, IOException {
        int userToUnfollowId = Integer.parseInt(request.getParameter("userId"));

        userController.unfollowUser(currentUser.getUserId(), userToUnfollowId);

        // Redirect back to the profile page
        String username = request.getParameter("username");
        response.sendRedirect(request.getContextPath() + "/user/profile/" + username);
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws SQLException, ServletException, IOException {
        try {
            String bio = request.getParameter("bio");
            // You can update more fields as needed

            currentUser.setBio(bio);
            userController.updateUser(currentUser);

            // Update the session user
            request.getSession().setAttribute("user", currentUser);

            // Redirect back to the profile
            response.sendRedirect(request.getContextPath() + "/user/profile/" + currentUser.getUsername());
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/user/edit-profile.jsp").forward(request, response);
        }
    }
}