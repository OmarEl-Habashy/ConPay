<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="aammo.ppv.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Feed</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/feed.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
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

<header class="navbar navbar-expand navbar-light bg-light mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/feed">PPV</a>

        <div class="d-flex align-items-center ms-auto">
            <div class="dropdown me-3">
                <button class="btn btn-outline-secondary position-relative" type="button" id="notificationDropdown"
                        data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-bell-fill"></i>
                    <span id="notification-badge" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger ${empty unreadNotifications ? 'd-none' : ''}">
                        ${unreadNotifications.size()}
                    </span>
                </button>
                <ul class="dropdown-menu dropdown-menu-end notification-dropdown" aria-labelledby="notificationDropdown" style="width: 300px; max-height: 400px; overflow-y: auto;">
                    <c:choose>
                        <c:when test="${empty notifications}">
                            <li><span class="dropdown-item text-center">No notifications</span></li>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="notification" items="${notifications}">
                                <li>
                                    <div class="dropdown-item notification-item">
                                        <div class="d-flex align-items-center">
                                            <div class="notification-avatar me-2">
                                                    ${notification.senderUsername.substring(0,1).toLowerCase()}
                                            </div>
                                            <div>
                                                <p class="mb-0">${notification.content}</p>
                                                <small class="text-muted">${notification.formattedTime}</small>
                                            </div>
                                        </div>
                                    </div>
                                </li>
                            </c:forEach>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-center" href="${pageContext.request.contextPath}/notifications">View all</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>

            <div class="dropdown">
                <button class="btn btn-outline-secondary d-flex align-items-center" type="button" id="userDropdown"
                        data-bs-toggle="dropdown" aria-expanded="false">
                    <div class="profile-pic-small me-2"><%= user.getUsername().substring(0,1).toLowerCase() %></div>
                    <%= user.getUsername() %>
                </button>
                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">Profile</a></li>
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                </ul>
            </div>
        </div>
    </div>
</header>
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
    <form action="${pageContext.request.contextPath}/feed" method="post" enctype="multipart/form-data">
        <textarea name="content" maxlength="280" placeholder="What's on your mind?" required></textarea>

        <div class="form-group">
            <label for="image">Add an image (optional)</label>
            <input type="file" id="image" name="image" accept="image/*">
            <div class="file-preview" id="imagePreview"></div>
        </div>

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
                    <!-- Handle both external URLs and local file paths -->
                    <c:choose>
                        <c:when test="${post.contentURL.startsWith('http')}">
                            <img src="${post.contentURL}" alt="Post media" style="max-width: 100%;"/>
                        </c:when>
                        <c:otherwise>
                            <img src="${pageContext.request.contextPath}${post.contentURL}" alt="Post media" style="max-width: 100%;"/>
                        </c:otherwise>
                    </c:choose>
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

    // Image preview script
    document.getElementById('image').addEventListener('change', function(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const preview = document.getElementById('imagePreview');
                preview.innerHTML = `<img src="${e.target.result}" alt="Preview" style="max-width: 100%; max-height: 200px;">`;
            }
            reader.readAsDataURL(file);
        }
    });
</script>

<%
    }
%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>