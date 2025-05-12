package aammo.ppv.servlet;

import aammo.ppv.dao.JdbcPostDAO;
import aammo.ppv.model.Comment;
import aammo.ppv.model.Post;
import aammo.ppv.model.User; // Add this import
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/viewPost")
public class ViewPostServlet extends HttpServlet {
    private JdbcPostDAO postDAO;

    @Override
    public void init() {
        postDAO = new JdbcPostDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int postId = Integer.parseInt(request.getParameter("postId"));
            Post post = postDAO.getPostById(postId);
            List<Comment> comments = postDAO.getCommentsByPostId(postId);
            int likeCount = postDAO.getLikeCountByPostId(postId);

            request.setAttribute("post", post);
            request.setAttribute("comments", comments);
            request.setAttribute("likeCount", likeCount);

            request.getRequestDispatcher("/WEB-INF/view/user/postDetails.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            int postId = Integer.parseInt(request.getParameter("postId"));
            User user = (User) request.getSession().getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if ("like".equals(action)) {
                if (!postDAO.hasUserLikedPost(postId, user.getUserId())) {
                    postDAO.insertLike(postId, user.getUserId());
                } else {
                    postDAO.removeLike(postId, user.getUserId());
                }
            } else if ("comment".equals(action)) {
                String content = request.getParameter("content");
                if (content != null && !content.trim().isEmpty()) {
                    postDAO.insertComment(postId, user.getUserId(), content);
                }
            }

            response.sendRedirect(request.getContextPath() + "/viewPost?postId=" + postId);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}
