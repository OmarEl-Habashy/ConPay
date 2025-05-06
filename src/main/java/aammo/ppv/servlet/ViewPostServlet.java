package aammo.ppv.servlet;

import aammo.ppv.dao.JdbcPostDAO;
import aammo.ppv.model.Comment;
import aammo.ppv.model.Post;
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
            List<Comment> comments = postDAO.getCommentsForPost(postId);
            int likeCount = postDAO.getLikeCount(postId);

            request.setAttribute("post", post);
            request.setAttribute("comments", comments);
            request.setAttribute("likeCount", likeCount);

            request.getRequestDispatcher("/WEB-INF/view/user/postDetails.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}
