<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="aammo.ppv.model.User" %>
<html>
<head>
    <title>Feed</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/feed.css">
</head>
<body>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
%>
<h2>You are not logged in. Please <a href="${pageContext.request.contextPath}/register">register</a>.</h2>
<%
} else {
%>
<h2>Welcome <%= user.getUsername() %> to the Feed Page!</h2>

<header></header>

<div class="profile-container">
    <div class="profile-pic">
        <%= user.getUsername() != null && !user.getUsername().isEmpty() ?
                user.getUsername().substring(0,1).toLowerCase() : "?" %>
    </div>
    <div class="profile-name"><%= user.getUsername() %></div>
    <div class="profile-handle">@<%= user.getUsername().toLowerCase() %></div>
    <div class="profile-bio">No bio provided</div>
    <div class="profile-stats">
        <div><span>0</span> Following</div>
        <div><span>0</span> Followers</div>
    </div>
    <button onclick="location.href='${pageContext.request.contextPath}/user/profile'">Edit profile</button>
</div>
<%
    }
%>
</body>
</html>