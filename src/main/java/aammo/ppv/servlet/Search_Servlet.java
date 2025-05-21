package aammo.ppv.servlet;

import aammo.ppv.controller.PostController;
import aammo.ppv.controller.UserController;
import aammo.ppv.dao.PostDAOFactory;
import aammo.ppv.dao.UserDAOFactory;
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
import java.util.List;

@WebServlet("/search")
public class Search_Servlet extends HttpServlet {
    private UserController userController;
    private PostController postController;

    @Override
    public void init() {
        userController = new UserController(UserDAOFactory.getUserDAO());
        postController = new PostController(PostDAOFactory.getPostDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Get user from session
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        // Get search query
        String searchQuery = request.getParameter("query");
        List<User> userResults = new ArrayList<>();
        List<Post> postResults = new ArrayList<>();

        // Perform search if query is not empty
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            try {
                // Search for users
                userResults = userController.searchUsersByUsername(searchQuery);

                // Search for posts
                postResults = postController.searchPostsByCaption(searchQuery);

            } catch (SQLException e) {
                System.err.println("Error in search: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Set attributes for the JSP
        request.setAttribute("searchQuery", searchQuery);
        request.setAttribute("userResults", userResults);
        request.setAttribute("postResults", postResults);

        // Forward to the search JSP
        request.getRequestDispatcher("/WEB-INF/view/search.jsp").forward(request, response);
    }
}