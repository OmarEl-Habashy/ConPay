package aammo.ppv.servlet;
import aammo.ppv.controller.PostController;
import aammo.ppv.dao.PostDAOFactory;
import aammo.ppv.model.Post;
import aammo.ppv.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/feed")
public class Feed_Servlet extends HttpServlet {
    private PostController postController;

    @Override
    public void init() {
        postController = new PostController(PostDAOFactory.getPostDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Get user from session
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            System.out.println("No user in session, redirecting to register");
            return; // Important: return here to stop execution
        }

        // Only get to this point if user is logged in
        try {
            // ONLY set posts attribute once
            List<Post> posts = postController.getFeedPosts(user.getUserId()); // Use your pagination method
            request.setAttribute("posts", posts);
        } catch (SQLException e) {
            System.err.println("Error fetching feed posts: " + e.getMessage());
            request.setAttribute("posts", new ArrayList<Post>());
        }

        // ONLY forward once
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

        String caption = request.getParameter("caption");
        String contentURL = request.getParameter("contentURL");

        try {
            Post post = new Post(0, user.getUserId(), contentURL, caption, new Date());
            postController.createPost(post);
            request.setAttribute("postMessage", "Post created successfully!");
        } catch (SQLException | IllegalArgumentException e) {
            request.setAttribute("postMessage", "Error: " + e.getMessage());
        }

        // New (redirects to GET /feed)
        response.sendRedirect(request.getContextPath() + "/feed");
    }
}