<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="aammo.ppv.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="aammo.ppv.controller.PostController" %>
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
        <a class="navbar-brand" href="${pageContext.request.contextPath}/feed">ConPay</a>

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
<%--<div class="theme-toggle">--%>
<%--    <label class="switch">--%>
<%--        <input type="checkbox" id="themeSwitch">--%>
<%--        <span class="slider round"></span>--%>
<%--    </label>--%>
<%--</div>--%>

<div class="search-bar-container">
    <form action="${pageContext.request.contextPath}/search" method="get">
        <input type="text" name="query" placeholder="Search users..." class="search-input">
        <button type="submit" class="search-button" style=" padding: 8px 12px;border: none;background-color: #4f62aa;color: white;border-radius: 20px;cursor: pointer;">Search</button>
    </form>
</div>

<!-- Replace the current profile-container and everything after it with this code -->
<div class="container-fluid">
    <div class="row">
        <div class="col-md-3">
            <div class="profile-container">
                <div class="profile-pic"><%= user.getUsername().substring(0,1).toLowerCase() %></div>
                <div class="profile-name"><%= user.getUsername() %></div>
                <div class="profile-handle">@<%= user.getUsername().toLowerCase() %></div>
                <div class="profile-bio"><%= user.getBio()%></div>
                <div class="profile-stats">
                    <div class="stat">
                        <a href="${pageContext.request.contextPath}/user/following/<%= user.getUsername() %>" style="text-decoration: none; color: inherit;">
                            <span class="stat-value"><%= request.getAttribute("followingCount") %></span> Following
                        </a>
                    </div>
                    <div class="stat">
                        <a href="${pageContext.request.contextPath}/user/followers/<%= user.getUsername() %>" style="text-decoration: none; color: inherit;">
                            <span class="stat-value"><%= request.getAttribute("followerCount") %></span> Followers
                        </a>
                    </div>
                </div>
                <button onclick="location.href='${pageContext.request.contextPath}/user/profile'">Profile</button>
                <button onclick="location.href='${pageContext.request.contextPath}/logout'">Logout</button>
            </div>
        </div>

        <!-- Main content area -->
        <div class="col-md-9">
            <div class="create-post-container">
                <form action="${pageContext.request.contextPath}/feed" method="post" enctype="multipart/form-data">
                    <textarea name="content" maxlength="280" placeholder="What's on your mind?" required></textarea>

                    <div class="form-actions">
                        <div class="media-options">
                            <div class="option-tabs">
                                <button type="button" class="tab-button active" id="localMediaTab">Upload Image</button>
                                <button type="button" class="tab-button" id="urlMediaTab">Image URL</button>
                            </div>

                            <div id="localMediaOption" class="media-option">
                                <label for="image" class="file-label">
                                    <i class="bi bi-image"></i> Add an image
                                </label>
                                <input type="file" id="image" name="image" accept="image/*" class="file-input">
                                <div class="file-preview" id="imagePreview"></div>
                            </div>

                            <div id="urlMediaOption" class="media-option" style="display: none;">
                                <input type="text" id="imageUrl" name="imageUrl" placeholder="Paste image URL here" class="url-input">
                                <div class="url-preview" id="urlPreview"></div>
                            </div>
                        </div>

                        <button type="submit" class="post-button">Post</button>
                    </div>
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
                            <a href="${pageContext.request.contextPath}/user/profile/${post.username}" class="post-username-link">
                                @${post.username}
                            </a>
                        </div>
                        <!-- Make the content area clickable to view post details -->
                        <a href="${pageContext.request.contextPath}/viewPost?postId=${post.postId}&source=feed" class="post-content-link">
                            <div class="post-caption">${post.caption}</div>
                            <c:if test="${not empty post.contentURL}">
                                <div class="post-media">
                                    <c:choose>
                                        <c:when test="${post.contentURL.startsWith('http')}">
                                            <img src="${post.contentURL}" alt="Post content">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}${post.contentURL}" alt="Post content">
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                        </a>
                        <div class="post-actions">
                            <form action="${pageContext.request.contextPath}/viewPost" method="post" class="like-form">
                                <input type="hidden" name="action" value="like">
                                <input type="hidden" name="postId" value="${post.postId}">
                                <input type="hidden" name="source" value="feed">
                                <button type="submit" class="like-btn">
                                    ❤ ${likeCounts[post.postId]} likes
                                </button>
                            </form>
                            <a href="${pageContext.request.contextPath}/viewPost?postId=${post.postId}&source=feed" class="comment-link">
                                <i class="bi bi-chat"></i> ${commentCounts[post.postId]} Comments
                            </a>
                        </div>
                        <div class="post-meta">
                            <span>Posted at: ${post.createdAt}</span>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
</div><script>
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
    const localMediaTab = document.getElementById('localMediaTab');
    const urlMediaTab = document.getElementById('urlMediaTab');
    const localMediaOption = document.getElementById('localMediaOption');
    const urlMediaOption = document.getElementById('urlMediaOption');

    localMediaTab.addEventListener('click', function() {
        localMediaTab.classList.add('active');
        urlMediaTab.classList.remove('active');
        localMediaOption.style.display = 'block';
        urlMediaOption.style.display = 'none';
        document.getElementById('imageUrl').value = '';
        document.getElementById('urlPreview').innerHTML = '';
    });

    urlMediaTab.addEventListener('click', function() {
        urlMediaTab.classList.add('active');
        localMediaTab.classList.remove('active');
        urlMediaOption.style.display = 'block';
        localMediaOption.style.display = 'none';
        document.getElementById('image').value = '';
        document.getElementById('imagePreview').innerHTML = '';
    });

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

    document.getElementById('imageUrl').addEventListener('input', debounce(function(e) {
        const url = e.target.value.trim();
        const preview = document.getElementById('urlPreview');

        if (url) {
            preview.innerHTML = '<div class="loading-preview">Loading preview...</div>';

            const img = new Image();
            img.onload = function() {
                preview.innerHTML = `<img src="${url}" alt="Preview" style="max-width: 100%; max-height: 200px;">`;
            };
            img.onerror = function() {
                preview.innerHTML = '<div class="error-preview">Invalid image URL</div>';
            };
            img.src = url;
        } else {
            preview.innerHTML = '';
        }
    }, 500));

    function debounce(func, wait) {
        let timeout;
        return function(...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), wait);
        };
    }
</script>

<%
    }
%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>