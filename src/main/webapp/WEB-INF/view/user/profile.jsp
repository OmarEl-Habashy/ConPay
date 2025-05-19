<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="aammo.ppv.model.User" %>
<%@ page import="aammo.ppv.model.Post" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getAttribute("profileUser") != null ? ((User) request.getAttribute("profileUser")).getUsername() : "Profile" %></title>
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
        <a href="${pageContext.request.contextPath}/feed" class="home-icon" title="Go to Feed">🏠</a>
    </div>

    <div class="profile-banner"></div>

    <div class="profile-info">
        <% User profileUser = (User) request.getAttribute("profileUser");
            if (profileUser != null) { %>
        <div class="profile-avatar">
            <%= profileUser.getUsername().charAt(0) %>
        </div>

        <div class="profile-name"><%= profileUser.getUsername() %></div>
        <div class="profile-username">@<%= profileUser.getUsername() %></div>

        <div class="profile-bio">
            <%= profileUser.getBio() != null && !profileUser.getBio().isEmpty() ? profileUser.getBio() : "<em>No bio provided</em>" %>
        </div>

        <div class="profile-stats">
            <div class="stat">
                <span class="stat-value"><%= request.getAttribute("followingCount") %></span> Following
            </div>
            <div class="stat">
                <span class="stat-value"><%= request.getAttribute("followerCount") %></span> Followers
            </div>
        </div>

        <% Boolean isOwnProfile = (Boolean) request.getAttribute("isOwnProfile");
            if (isOwnProfile != null && isOwnProfile) { %>
        <form action="${pageContext.request.contextPath}/user/editProfile" method="get">
            <button type="submit" class="btn btn-edit">Edit profile</button>
        </form>
        <% } else {
            Boolean isFollowing = (Boolean) request.getAttribute("isFollowing");
            if (isFollowing != null && isFollowing) { %>
        <form action="${pageContext.request.contextPath}/user/profile" method="post">
            <input type="hidden" name="action" value="unfollow">
            <input type="hidden" name="userId" value="<%= profileUser.getUserId() %>">
            <button type="submit" class="btn btn-unfollow">Unfollow</button>
        </form>
        <% } else { %>
        <form action="${pageContext.request.contextPath}/user/profile" method="post">
            <input type="hidden" name="action" value="follow">
            <input type="hidden" name="userId" value="<%= profileUser.getUserId() %>">
            <button type="submit" class="btn btn-follow">Follow</button>
        </form>
        <% } } } %>
    </div>

    <div class="tweets-container">
        <% List<Post> userPosts = (List<Post>) request.getAttribute("userPosts");
            if (userPosts != null && !userPosts.isEmpty()) {
                for (Post post : userPosts) { %>
        <div class="tweet">
            <div class="tweet-content">
                <%= post.getCaption() %>
            </div>
            <div class="tweet-date">
                <%= post.getCreatedAt() %>
            </div>
        </div>
        <% } } else { %>
        <div class="no-tweets">No tweets yet</div>
        <% } %>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const themeSwitch = document.getElementById('themeSwitch');
        const body = document.body;
        const savedTheme = localStorage.getItem('theme');

        if (savedTheme === 'dark') {
            body.classList.add('dark-mode');
            if (themeSwitch) themeSwitch.checked = true;
        }

        if (themeSwitch) {
            themeSwitch.addEventListener('change', function () {
                if (this.checked) {
                    body.classList.add('dark-mode');
                    localStorage.setItem('theme', 'dark');
                } else {
                    body.classList.remove('dark-mode');
                    localStorage.setItem('theme', 'light');
                }
            });
        }
    });
</script>
</body>
</html>