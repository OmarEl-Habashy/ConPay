package aammo.ppv.servlet;
import aammo.ppv.controller.UserController;

import aammo.ppv.dao.UserDAOFactory;
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
        // Use the factory to get the DAO implementation
        userController = new UserController(UserDAOFactory.getUserDAO());
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
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if (action == null) {
                listUsers(request, response);
            } else {
                switch (action) {
                    case "view":
                        viewProfile(request, response);
                        break;
                    default:
                        listUsers(request, response);
                        break;
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    // returns users
    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<User> listUsers = userController.selectAllUsers();
        request.setAttribute("userList", listUsers);
        request.getRequestDispatcher("user-list.jsp").forward(request, response);
    }

    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String bio = request.getParameter("bio");

            User newUser = new User(0, username, email, password, bio, null);
            userController.insertUser(newUser);
            response.sendRedirect("users");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("create-user.jsp").forward(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String bio = request.getParameter("bio");

            User user = new User(userId, username, email, password, bio, null);
            userController.updateUser(user);
            response.sendRedirect("users");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("edit-user.jsp").forward(request, response);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        userController.deleteUser(userId);
        response.sendRedirect("users");
    }

    private void viewProfile(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        User user = userController.selectUser(userId);

        if (user != null) {
            request.setAttribute("user", user);
            request.getRequestDispatcher("profile.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }
}