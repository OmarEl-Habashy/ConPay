<%-- filepath: src/main/webapp/WEB-INF/view/profile.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="aammo.ppv.model.User" %>
<%@ page import="aammo.ppv.model.Post" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%
        User profileUser = (User) request.getAttribute("profileUser");
        String title = profileUser != null ? profileUser.getUsername() + " - Twitter Clone" : "Profile";
    %>
    <title><%= title %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        /* Quick inline styles - ideally these would be in your CSS file */
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f7f9fa;
            color: #0f1419;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background: white;
            border-left: 1px solid #e6ecf0;
            border-right: 1px solid #e6ecf0;
            min-height: 100vh;
        }
        .profile-header {
            padding: 15px;
            border-bottom: 1px solid #e6ecf0;
            position: relative;
        }
        .profile-banner {
            background-color: #1DA1F2;
            height: 150px;
            width: 100%;
        }
        .profile-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            border: 4px solid white;
            margin-top: -60px;
            background-color: #f2f2f2;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 50px;
            color: #657786;
        }
        .profile-info {
            padding: 15px;
        }
        .profile-name {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        .profile-username {
            font-size: 15px;
            color: #657786;
            margin-bottom: 10px;
        }
        .profile-bio {
            margin-bottom: 15px;
        }
        .profile-stats {
            display: flex;
            margin-bottom: 15px;
        }
        .stat {
            margin-right: 20px;
            font-size: 14px;
        }
        .stat-value {
            font-weight: bold;
        }
        .btn {
            padding: 8px 16px;
            border-radius: 30px;
            font-weight: bold;
            cursor: pointer;
            font-size: 14px;
            border: 1px solid;
        }
        .btn-follow {
            background-color: #1DA1F2;
            color: white;
            border-color: #1DA1F2;
        }
        .btn-unfollow {
            background-color: white;
            color: #1DA1F2;
            border-color: #1DA1F2;
        }
        .btn-edit {
            background-color: white;
            color: #0f1419;
            border-color: #cfd9de;
        }
        .tweets-container {
            border-top: 1px solid #e6ecf0;
        }
        .tweet {
            padding: 15px;
            border-bottom: 1px solid #e6ecf0;
        }
        .tweet-content {
            margin-bottom: 10px;
        }
        .tweet-date {
            font-size: 14px;
            color: #657786;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="profile-header">
        <h1>Profile</h1>
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
        <form action="${pageContext.request.contextPath}/profile" method="get">
            <input type="hidden" name="action" value="edit">
            <button type="submit" class="btn btn-edit">Edit profile</button>
        </form>
        <% } else { %>
        <%
            Boolean isFollowing = (Boolean) request.getAttribute("isFollowing");
            if (isFollowing != null && isFollowing) {
        %>
        <form action="${pageContext.request.contextPath}/profile" method="post">
            <input type="hidden" name="action" value="unfollow">
            <input type="hidden" name="userId" value="<%= profileUser.getUserId() %>">
            <input type="hidden" name="username" value="<%= profileUser.getUsername() %>">
            <button type="submit" class="btn btn-unfollow">Unfollow</button>
        </form>
        <% } else { %>
        <form action="${pageContext.request.contextPath}/profile" method="post">
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
        <div class="tweet">
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
            No tweets yet
        </div>
        <% } %>
    </div>
</div>
</body>
</html>