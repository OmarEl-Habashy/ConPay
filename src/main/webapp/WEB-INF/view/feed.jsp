<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="aammo.ppv.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<header></header>

<div class="profile-container">
    <div class="profile-pic"><%= user.getUsername().substring(0,1).toLowerCase() %></div>
    <div class="profile-name"><%= user.getUsername() %></div>
    <div class="profile-handle">@<%= user.getUsername().toLowerCase() %></div>
    <div class="profile-bio">No bio provided</div>
    <div class="profile-stats">
        <div><span>0</span> Following</div>
        <div><span>0</span> Followers</div>
    </div>
    <button onclick="location.href='${pageContext.request.contextPath}/user/profile'">Profile</button>
    <button onclick="location.href='${pageContext.request.contextPath}/logout'">Logout</button>
</div>

<!-- Create Post Form -->
<div class="create-post-container">
    <form action="${pageContext.request.contextPath}/feed" method="post">
        <textarea name="caption" maxlength="280" placeholder="What's on your mind?" required></textarea>
        <input type="url" name="contentURL" placeholder="Optional: Image/Video URL">
        <button type="submit">Post</button>
    </form>
    <c:if test="${not empty requestScope.postMessage}">
        <div class="post-message">${requestScope.postMessage}</div>
    </c:if>
</div>

<!-- Feed Posts -->
<div class="feed-posts">
    <c:forEach var="post" items="${posts}">
        <div class="post">
            <div class="post-header">
                <span class="post-username">@${post.userId}</span>
            </div>
            <div class="post-caption">${post.caption}</div>
            <c:if test="${not empty post.contentURL}">
                <div class="post-media">
                    <img src="${post.contentURL}" alt="Post media" style="max-width: 100%;"/>
                </div>
            </c:if>
            <div class="post-meta">
                <span>Posted at: ${post.createdAt}</span>
            </div>
        </div>
    </c:forEach>
</div>

<%
    }
%>
</body>
</html>