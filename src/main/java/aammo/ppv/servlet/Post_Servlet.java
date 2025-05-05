package aammo.ppv.servlet;

import aammo.ppv.controller.PostController;
import aammo.ppv.dao.JdbcPostDAO;
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

@WebServlet("/postAction")
public class Post_Servlet extends HttpServlet {
    private PostController postController;


    @Override
    public void init() {
        postController = new PostController(PostDAOFactory.getPostDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String postIdParam = request.getParameter("postId");
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to perform this action.");
            return;
        }

        if (action == null || postIdParam == null || postIdParam.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters.");
            return;
        }

        try {
            int postId = Integer.parseInt(postIdParam);

            switch (action) {
                case "like":
                    handleLikeAction(postId, user.getUserId(), response);
                    break;
                case "comment":
                    handleCommentAction(postId, user.getUserId(), request, response);
                    break;
                case "delete":
                    handleDeleteAction(postId, user.getUserId(), response);
                    break;
                case "update":
                    handleUpdateAction(postId, user.getUserId(), request, response);
                    break;
                default:
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid action!");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid post ID format.");
        } catch (SQLException e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    private void handleLikeAction(int postId, int userId, HttpServletResponse response) throws SQLException, IOException {
        postController.insertLike(postId, userId);
        sendSuccessResponse(response, "Post liked successfully!");
    }

    private void handleCommentAction(int postId, int userId, HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String content = request.getParameter("content");
        if (content == null || content.trim().isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Comment content cannot be empty.");
            return;
        }
        postController.insertComment(postId, userId, content.trim());
        sendSuccessResponse(response, "Comment added successfully!");
    }

    private void handleDeleteAction(int postId, int userId, HttpServletResponse response) throws SQLException, IOException {
        Post post = postController.getPostById(postId);
        if (post == null || post.getUserId() != userId) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "You are not authorized to delete this post.");
            return;
        }
        postController.deletePost(postId, userId);
        sendSuccessResponse(response, "Post deleted successfully!");
    }

    private void handleUpdateAction(int postId, int userId, HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String newCaption = request.getParameter("caption");
        if (newCaption == null || newCaption.trim().isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Caption cannot be empty.");
            return;
        }
        Post post = postController.getPostById(postId);
        if (post == null || post.getUserId() != userId) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "You are not authorized to update this post.");
            return;
        }
        post.setCaption(newCaption.trim());
        postController.updatePost(post, userId);
        sendSuccessResponse(response, "Post updated successfully!");
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(message);
    }

    private void sendSuccessResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(message);
    }
}