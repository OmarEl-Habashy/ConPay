package aammo.ppv.servlet;

import aammo.ppv.controller.PostController;
import aammo.ppv.controller.UserController;
import aammo.ppv.dao.PostDAOFactory;
import aammo.ppv.dao.UserDAOFactory;
import aammo.ppv.model.Comment;
import aammo.ppv.model.Post;
import aammo.ppv.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/viewPost")
public class ViewPostServlet extends HttpServlet {
    private PostController postController;
    private static final String LOG_FILE = System.getProperty("user.dir") + "/logs/post_actions.log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void init() {
        postController = new PostController(PostDAOFactory.getPostDAO());

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        try {
            int postId = Integer.parseInt(request.getParameter("postId"));
            Post post = postController.getPostById(postId);

            if (post != null && post.getUsername() == null) {
                // You'll need a UserController instance
                UserController userController = new UserController(UserDAOFactory.getUserDAO());
                User postUser = userController.selectUser(post.getUserId());
                if (postUser != null) {
                    // Either set it directly if you added username field to Post
                    // post.setUsername(postUser.getUsername());

                    // Or add it as a separate attribute if you can't modify Post
                    request.setAttribute("postUsername", postUser.getUsername());
                }
            }
            List<Comment> comments = postController.getCommentsForPost(postId);
            int likeCount = postController.getLikeCount(postId);
            // Log the post view
            User user = (User) request.getSession().getAttribute("user");
            String username = user != null ? user.getUsername() : "Guest";
            logAction("POST_VIEW", "User=" + username + ", PostID=" + postId);

            request.setAttribute("post", post);
            request.setAttribute("comments", comments);
            request.setAttribute("likeCount", likeCount);

            request.getRequestDispatcher("/WEB-INF/view/user/postDetails.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            logAction("ERROR", "Database error: " + e.getMessage());
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            String action = request.getParameter("action");
            int postId = Integer.parseInt(request.getParameter("postId"));
            User user = (User) request.getSession().getAttribute("user");

            if (user == null) {
                logAction("AUTH_ERROR", "Unauthorized post action attempt");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if ("like".equals(action)) {
                // Use toggleLike method from PostController
                postController.toggleLike(postId, user.getUserId());
                boolean hasLiked = postController.hasUserLikedPost(postId, user.getUserId());
                logAction(hasLiked ? "LIKE" : "UNLIKE", "User=" + user.getUsername() + ", PostID=" + postId);
            } else if ("comment".equals(action)) {
                String content = request.getParameter("content");
                if (content != null && !content.trim().isEmpty()) {
                    postController.insertComment(postId, user.getUserId(), content);
                    logAction("COMMENT", "User=" + user.getUsername() + ", PostID=" + postId +
                            ", Content=" + content);
                }
            }

            response.sendRedirect(request.getContextPath() + "/viewPost?postId=" + postId);
        } catch (SQLException e) {
            logAction("ERROR", "Database error: " + e.getMessage());
            throw new ServletException("Database error", e);
        }
    }

    private void logAction(String actionType, String details) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            writer.println("[" + timestamp + "] " + actionType + ": " + details);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}