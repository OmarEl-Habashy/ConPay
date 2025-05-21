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

<!-- 🌗 Theme Toggle Switch (you can move this to header or wherever you want) -->
<div class="theme-toggle">
    <label class="switch">
        <input type="checkbox" id="themeSwitch">
        <span class="slider round"></span>
    </label>
</div>

<!-- 🔍 Search Bar -->
<div class="search-bar-container">
    <form action="${pageContext.request.contextPath}/search" method="get">
        <input type="text" name="query" placeholder="Search users..." class="search-input">
        <button type="submit" class="search-button">Search</button>
    </form>
</div>

<!-- 👤 Profile Section -->
<div class="profile-container">
    <div class="profile-pic"><%= user.getUsername().substring(0,1).toLowerCase() %></div>
    <div class="profile-name"><%= user.getUsername() %></div>
    <div class="profile-handle">@<%= user.getUsername().toLowerCase() %></div>
    <div class="profile-bio"><%= user.getBio()%></div>
    <div class="profile-stats">
        <div><span>${followingCount}</span> Following</div>
        <div><span>${followerCount}</span> Followers</div>
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

<div class="feed-posts">
    <c:forEach var="post" items="${posts}">
        <div class="post" onclick="window.location.href='${pageContext.request.contextPath}/viewPost?postId=${post.postId}'" style="cursor: pointer;">
            <div class="post-header">
                <span class="post-username">@${post.username}</span>
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

<!-- Feed Posts -->
<div class="feed-posts">
    <c:forEach var="post" items="${posts}">
        <div class="post">
            <div class="post-header">
                <span class="post-username">@${post.username}</span>
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
<!-- interactions -->
<div class="feed-posts">
    <c:forEach var="post" items="${posts}">
        <div class="post" onclick="window.location.href='${pageContext.request.contextPath}/viewPost?postId=${post.postId}'" style="cursor: pointer;">
            <div class="post-header">
                <span class="post-username">@${post.username}</span>
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

<!-- 🌙 Dark Mode JavaScript -->
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

<%
    }
%>
</body>
</html>
