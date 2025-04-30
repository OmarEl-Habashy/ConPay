package aammo.ppv.controller;
import aammo.ppv.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private UserController userController;

    @Override
    public void init() {
        userController = new UserController();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            switch (action) {
                case "create":
                    insertUser(request, response);
                    break;
                case "update":
                    updateUser(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                default:
                    response.sendRedirect("user-list.jsp");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            listUsers(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<User> listUsers = userController.selectAllUsers();
        request.setAttribute("userList", listUsers);
        request.getRequestDispatcher("user-list.jsp").forward(request, response);
    }


    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String bio = request.getParameter("bio");

        User newUser = new User(0, username, email, password, bio, null);
        userController.insertUser(newUser);
        response.sendRedirect("users");
    }


    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String bio = request.getParameter("bio");

        User user = new User(userId, username, email, password, bio, null);
        userController.updateUser(user);
        response.sendRedirect("users");
    }


    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        userController.deleteUser(userId);
        response.sendRedirect("users");
    }
}