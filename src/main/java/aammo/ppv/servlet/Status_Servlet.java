package aammo.ppv.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/status/*")
public class Status_Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extract the post ID from the URL path
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            // Remove the leading slash
            String postId = pathInfo.substring(1);

            // Forward to the post servlet
            request.getRequestDispatcher("/post?id=" + postId).forward(request, response);
        } else {
            // If no post ID is provided, redirect to the feed
            response.sendRedirect(request.getContextPath() + "/feed");
        }
    }
}