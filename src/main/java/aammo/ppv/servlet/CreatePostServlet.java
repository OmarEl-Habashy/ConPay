package aammo.ppv.servlet;

import aammo.ppv.controller.PostController;
import aammo.ppv.controller.UserController;
import aammo.ppv.dao.JdbcPostDAO;
import aammo.ppv.dao.UserDAOFactory;
import aammo.ppv.model.Post;
import aammo.ppv.model.User;
import aammo.ppv.service.MediaService;
import aammo.ppv.service.LogManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

@WebServlet("/create-post")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)


public class CreatePostServlet extends HttpServlet {

    private final MediaService mediaService = new MediaService();
    private final PostController postController = new PostController(new JdbcPostDAO());


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/view/post/create-post.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String content = request.getParameter("content");
        Part filePart = request.getPart("image");

        try {
            String mediaUrl = null;
            if (filePart != null && filePart.getSize() > 0) {
                mediaUrl = mediaService.saveMedia(filePart, user.getUserId());
            }

            Post post = new Post(0, user.getUserId(), mediaUrl, content, new Date());

            postController.createPost(post);

            LogManager.logAction("POST_CREATED", "User=" + user.getUserId() + ", PostId=" + post.getPostId());

            response.sendRedirect(request.getContextPath() + "/feed");

        } catch (IOException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("content", content);
            request.getRequestDispatcher("/WEB-INF/view/post/create-post.jsp").forward(request, response);
        } catch (SQLException e) {
            LogManager.logAction("POST_CREATION_FAILED", "User=" + user.getUserId() + ", Error=" + e.getMessage());
            request.setAttribute("errorMessage", "Failed to create post. Please try again.");
            request.setAttribute("content", content);
            request.getRequestDispatcher("/WEB-INF/view/post/create-post.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("content", content);
            request.getRequestDispatcher("/WEB-INF/view/post/create-post.jsp").forward(request, response);
        }
    }
}