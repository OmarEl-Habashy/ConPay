package aammo.ppv.servlet;
import aammo.ppv.controller.NotificationController;
import aammo.ppv.dao.NotificationDAOFactory;
import aammo.ppv.model.Notification;
import aammo.ppv.controller.PostController;
import aammo.ppv.controller.UserController;
import aammo.ppv.dao.PostDAOFactory;
import aammo.ppv.dao.UserDAOFactory;
import aammo.ppv.model.Post;
import aammo.ppv.model.User;
import aammo.ppv.service.MediaService;
import aammo.ppv.service.LogManager;
import aammo.ppv.service.NotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/feed")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1 MB
        maxFileSize = 1024 * 1024 * 10,   // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class Feed_Servlet extends HttpServlet {
    private PostController postController;
    private final MediaService mediaService = new MediaService();

    @Override
    public void init() {
        postController = new PostController(PostDAOFactory.getPostDAO());
        NotificationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Get user from session
        User user = (User) request.getSession().getAttribute("user");
        UserController userController = new UserController(UserDAOFactory.getUserDAO());
        int followingCount = 0;
        try {
            followingCount = userController.getFollowingCount(user.getUserId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int followerCount = 0;
        try {
            followerCount = userController.getFollowerCount(user.getUserId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        request.setAttribute("followingCount", followingCount);
        request.setAttribute("followerCount", followerCount);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            System.out.println("No user in session, redirecting to register");
            return;
        }

        // Get notifications for the user
        try {
            NotificationController notificationController = new NotificationController(
                    NotificationDAOFactory.getNotificationDAO());
            List<Notification> notifications = notificationController.getUserNotifications(user.getUserId());
            request.setAttribute("notifications", notifications);
            List<Notification> unreadNotifications = notificationController.getUnreadNotifications(user.getUserId());
            request.setAttribute("unreadNotifications", unreadNotifications);
        } catch (SQLException e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
            request.setAttribute("notifications", new ArrayList<>());
            request.setAttribute("unreadNotifications", new ArrayList<>());
        }
        try {
            List<Post> posts = postController.getFeedPosts(user.getUserId()); // Use your pagination method
            request.setAttribute("posts", posts);
        } catch (SQLException e) {
            System.err.println("Error fetching feed posts: " + e.getMessage());
            request.setAttribute("posts", new ArrayList<Post>());
        }

        System.out.println("Posts in request: " + ((List)request.getAttribute("posts")).size());
        request.getRequestDispatcher("/WEB-INF/view/feed.jsp").forward(request, response);
        System.out.println("Feed invoked for user: " + user.getUsername());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        String content = request.getParameter("content");
        Part filePart = request.getPart("image");

        System.out.println("Processing post from user: " + user.getUsername());
        System.out.println("Content: " + content);
        System.out.println("File part present: " + (filePart != null));
        if (filePart != null) {
            System.out.println("File size: " + filePart.getSize());
            System.out.println("File name: " + filePart.getSubmittedFileName());
        }

        try {
            String mediaUrl = null;
            if (filePart != null && filePart.getSize() > 0) {
                mediaUrl = mediaService.saveMedia(filePart, user.getUserId());
                System.out.println("Media saved with URL: " + mediaUrl);
            }
            Post post = new Post(0, user.getUserId(), mediaUrl, content, new Date());
            postController.createPost(post);
            System.out.println("Post created with ID: " + post.getPostId());

            LogManager.logAction("POST_CREATED", "User=" + user.getUserId() + ", PostId=" + post.getPostId());
            request.setAttribute("postMessage", "Post created successfully!");
        } catch (IOException e) {
            System.err.println("Media upload error: " + e.getMessage());
            LogManager.logAction("MEDIA_UPLOAD_FAILED", "User=" + user.getUserId() + ", Error=" + e.getMessage());
            request.setAttribute("postMessage", "Error uploading media: " + e.getMessage());
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Post creation error: " + e.getMessage());
            LogManager.logAction("POST_CREATION_FAILED", "User=" + user.getUserId() + ", Error=" + e.getMessage());
            request.setAttribute("postMessage", "Error creating post: " + e.getMessage());
        }

        // Redirect to GET /feed
        response.sendRedirect(request.getContextPath() + "/feed");
    }
}
