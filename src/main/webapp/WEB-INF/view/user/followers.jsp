<%--
  Created by IntelliJ IDEA.
  User: omare
  Date: 5/26/2025
  Time: 10:26 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="aammo.ppv.model.User" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%
        User profileUser = (User) request.getAttribute("profileUser");
        String title = profileUser != null ? "Followers of " + profileUser.getUsername() : "Followers";
    %>
    <title><%= title %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/profile.css">
    <style>
        .user-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .user-item {
            padding: 15px 20px;
            border-bottom: 1px solid #e1e8ed;
            display: flex;
            align-items: center;
        }
        .user-item:last-child {
            border-bottom: none;
        }
        .user-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background-color: #ccc;
            margin-right: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #fff;
        }
        .user-info {
            flex: 1;
        }
        .username {
            font-weight: bold;
            text-decoration: none;
            color: #14171a;
        }
        .username:hover {
            color: #1da1f2;
        }
        .empty-message {
            text-align: center;
            padding: 30px;
            color: #657786;
        }
        .back-link {
            margin-top: 10px;
            display: inline-block;
            color: #1da1f2;
            text-decoration: none;
        }
    </style>
</head>
<body>
<div class="theme-toggle">
    <label class="switch">
        <input type="checkbox" id="themeSwitch">
        <span class="slider round"></span>
    </label>
</div>
<div class="container">
    <div class="profile-header">
        <h1>Followers of <%= profileUser.getUsername() %></h1>
        <a href="${pageContext.request.contextPath}/user/profile/<%= profileUser.getUsername() %>" class="back-link">
            Back to Profile
        </a>
    </div>

    <%
        List<User> userList = (List<User>) request.getAttribute("userList");
        if (userList != null && !userList.isEmpty()) {
    %>
    <ul class="user-list">
        <% for (User user : userList) { %>
        <li class="user-item">
            <div class="user-avatar">
                <%= user.getUsername().toUpperCase().charAt(0) %>
            </div>
            <div class="user-info">
                <a href="${pageContext.request.contextPath}/user/profile/<%= user.getUsername() %>" class="username">
                    <%= user.getUsername() %>
                </a>
            </div>
        </li>
        <% } %>
    </ul>
    <% } else { %>
    <p class="empty-message">No followers yet.</p>
    <% } %>

    <script>
        const toggle = document.getElementById("themeSwitch");
        const body = document.body;

        toggle.addEventListener("change", function () {
            body.classList.toggle("dark-mode", this.checked);
            localStorage.setItem("theme", this.checked ? "dark" : "light");
        });

        const savedTheme = localStorage.getItem("theme");
        if (savedTheme === "dark") {
            toggle.checked = true;
            body.classList.add("dark-mode");
        }
    </script>
</div>
</body>
</html>
