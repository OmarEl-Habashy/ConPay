<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="aammo.ppv.model.User" %>
<%@ page import="aammo.ppv.model.Post" %>
<%@ page import="java.util.List" %>
<!-- This is an HTML comment -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%
        User profileUser = (User) request.getAttribute("profileUser");
        String title = profileUser != null ? profileUser.getUsername() : "Profile";
    %>
    <title><%= title %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/profile.css">
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
        <h1>Profile</h1>
        <a href="${pageContext.request.contextPath}/feed" class="home-icon" title="Go to Home">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="home-icon-svg">
                <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
            </svg>
            <span style="display: none;">Home</span>
        </a>
    </div>

    <div class="profile-banner"></div>

    <div class="profile-info">
        <% if (profileUser != null && profileUser.getUsername() != null && !profileUser.getUsername().isEmpty()) { %>
        <div class="profile-avatar">
            <%= profileUser.getUsername().charAt(0) %>
        </div>

        <div class="profile-name"><%= profileUser.getUsername() %></div>
        <div class="profile-username">@<%= profileUser.getUsername() %></div>

        <div class="profile-bio">
            <% if (profileUser.getBio() != null && !profileUser.getBio().isEmpty()) { %>
            <%= profileUser.getBio() %>
            <% } else { %>
            <em>No bio provided</em>
            <% } %>
        </div>

        <div class="profile-stats">
            <div class="stat">
                <span class="stat-value"><%= request.getAttribute("followingCount") %></span> Following
            </div>
            <div class="stat">
                <span class="stat-value"><%= request.getAttribute("followerCount") %></span> Followers
            </div>
        </div>

        <%
            Boolean isOwnProfile = (Boolean) request.getAttribute("isOwnProfile");
            if (isOwnProfile != null && isOwnProfile) {
        %>
        <form action="${pageContext.request.contextPath}/user/editProfile" method="get">
            <input type="hidden" name="action" value="edit">
            <button type="submit" class="btn btn-edit">Edit profile</button>
        </form>
        <% } else { %>
        <%
            Boolean isFollowing = (Boolean) request.getAttribute("isFollowing");
            if (isFollowing != null && isFollowing) {
        %>
        <form action="${pageContext.request.contextPath}/user/profile" method="post">
            <input type="hidden" name="action" value="unfollow">
            <input type="hidden" name="userId" value="<%= profileUser.getUserId() %>">
            <input type="hidden" name="username" value="<%= profileUser.getUsername() %>">
            <button type="submit" class="btn btn-unfollow">Unfollow</button>
        </form>
        <% } else { %>
        <form action="${pageContext.request.contextPath}/user/profile" method="post">
            <input type="hidden" name="action" value="follow">
            <input type="hidden" name="userId" value="<%= profileUser.getUserId() %>">
            <input type="hidden" name="username" value="<%= profileUser.getUsername() %>">
            <button type="submit" class="btn btn-follow">Follow</button>
        </form>
        <% } %>
        <% } %>
        <% } %>
    </div>

    <div class="tweets-container">
        <%
            List<Post> userPosts = (List<Post>) request.getAttribute("userPosts");
            if (userPosts != null && !userPosts.isEmpty()) {
                for (Post post : userPosts) {
        %>
        <div class="tweet" onclick="window.location.href='${pageContext.request.contextPath}/viewPost?postId=<%= post.getPostId() %>'" style="cursor: pointer;">
            <% if (post.getContentURL() != null && !post.getContentURL().isEmpty()) { %>
            <div class="post-media">
                <img src="${pageContext.request.contextPath}<%= post.getContentURL() %>" alt="Post media" style="max-width: 100%;"/>
            </div>
            <% } %>
            <div class="tweet-content">
                <%= post.getCaption() %>
            </div>
            <div class="tweet-date">
                <%= post.getCreatedAt() %>
            </div>
        </div>
        <%
            }
        } else {
        %>
        <div style="padding: 20px; text-align: center; color: #657786;">
            No posts yet
        </div>
        <% } %>
    </div>
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