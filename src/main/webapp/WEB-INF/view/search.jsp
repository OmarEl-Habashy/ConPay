<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/feed.css">
    <style>
        /* Search Styles */
        .main-search-container {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            width: 100%;
        }

        .search-header {
            background: #fff;
            padding: 24px 0 16px 0;
            box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
        }

        .search-bar-container {
            display: flex;
            align-items: center;
            width: 100%;
            max-width: 500px;
            margin: 0 16px;
        }

        .search-input {
            flex: 1;
            padding: 12px 16px;
            border: 1px solid #ccc;
            border-radius: 24px 0 0 24px;
            font-size: 16px;
            outline: none;
            background: #f9f9f9;
            transition: border 0.2s;
        }

        .search-input:focus {
            border: 1.5px solid #4267B2;
        }

        .search-button {
            background: #4267B2;
            border: none;
            border-radius: 0 24px 24px 0;
            padding: 0 20px;
            height: 44px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background 0.2s;
            color: white;
        }

        .search-button:hover {
            background: #365899;
        }

        .back-link {
            position: absolute;
            left: 24px;
            top: 50%;
            transform: translateY(-50%);
            color: #4267B2;
            text-decoration: none;
            font-weight: bold;
            font-size: 16px;
        }

        .search-content {
            flex: 1;
            width: 100%;
            max-width: 700px;
            margin: 0 auto;
            padding: 32px 16px 24px 16px;
        }

        .search-section {
            margin-bottom: 36px;
        }

        .search-title {
            font-size: 1.2em;
            margin-bottom: 12px;
            font-weight: bold;
        }

        .user-result, .post-result {
            padding: 18px 20px;
            border-bottom: 1px solid #eee;
            background: #fff;
            border-radius: 10px;
            margin-bottom: 14px;
            transition: background 0.2s;
            box-shadow: 0 1px 4px rgba(0,0,0,0.03);
        }

        .user-result:hover, .post-result:hover {
            background: #e3e8ff;
        }

        .no-results {
            color: #888;
            text-align: center;
            margin: 20px 0;
        }

        .profile-handle {
            color: #777;
            font-size: 13px;
        }

        .profile-bio {
            color: #444;
            font-size: 14px;
            margin-top: 4px;
        }

        .post-meta {
            font-size: 13px;
            color: #888;
            margin-bottom: 5px;
        }

        .post-result img {
            margin-top: 8px;
            border-radius: 8px;
            max-width: 100%;
            max-height: 220px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.07);
        }

        .post-link {
            text-decoration: none;
            color: inherit;
            display: block;
        }

        .post-link:visited {
            color: inherit;
        }

        /* Dark mode overrides */
        body.dark-mode .search-header {
            background: #1e1e1e;
            box-shadow: 0 2px 8px rgba(0,0,0,0.18);
        }

        body.dark-mode .search-content {
            background: transparent;
        }

        body.dark-mode .user-result,
        body.dark-mode .post-result {
            background: #232323;
            border-bottom: 1px solid #222;
            color: #e0e0e0;
        }

        body.dark-mode .user-result:hover,
        body.dark-mode .post-result:hover {
            background: #232f4b;
        }

        body.dark-mode .search-input {
            background: #232323;
            border: 1px solid #444;
            color: #e0e0e0;
        }

        body.dark-mode .search-title {
            color: #bfcaff;
        }

        body.dark-mode .profile-handle {
            color: #aaa;
        }

        body.dark-mode .profile-bio {
            color: #bbb;
        }

        body.dark-mode .post-meta {
            color: #aaa;
        }

        body.dark-mode .no-results {
            color: #aaa;
        }
    </style>
</head>
<body>
<div class="main-search-container">
    <div class="search-header">
        <a href="${pageContext.request.contextPath}/feed" class="back-link">&larr; Feed</a>
        <div class="search-bar-container">
            <form action="${pageContext.request.contextPath}/search" method="get">
                <input type="text" name="query" placeholder="Search users or posts..." class="search-input" value="${searchQuery}">
                <button type="submit" class="search-button">Search</button>
            </form>
        </div>
        <div class="theme-toggle">
            <label class="switch">
                <input type="checkbox" id="themeSwitch">
                <span class="slider round"></span>
            </label>
        </div>
    </div>

    <div class="search-content">
        <h2 style="margin-bottom: 24px;">Search</h2>
        <div class="subtitle" style="margin-bottom: 32px;">Find users or posts</div>

        <c:if test="${not empty searchQuery}">
            <div class="search-section">
                <div class="search-title">User Results</div>
                <c:choose>
                    <c:when test="${not empty userResults}">
                        <c:forEach var="user" items="${userResults}">
                            <div class="user-result">
                                <strong>
                                    <a href="${pageContext.request.contextPath}/user/profile/${user.username}" style="color:#4267B2;">
                                            ${user.username}
                                    </a>
                                </strong>
                                <div class="profile-handle">@${user.username.toLowerCase()}</div>
                                <div class="profile-bio">${user.bio}</div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="no-results">No users found.</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="search-section">
                <div class="search-title">Post Results</div>
                <c:choose>
                    <c:when test="${not empty postResults}">
                        <c:forEach var="post" items="${postResults}">
                            <a href="${pageContext.request.contextPath}/viewPost?postId=${post.postId}" class="post-link">
                                <div class="post-result">
                                    <div class="caption">${post.caption}</div>
                                    <div class="post-meta">
                                        <span>By
                                            <a href="${pageContext.request.contextPath}/user/profile/${post.username}" style="color:#4267B2;" onclick="event.stopPropagation();">
                                                    ${post.username}
                                            </a>
                                        </span>
                                        <span> | ${post.createdAt}</span>
                                    </div>
                                    <c:if test="${not empty post.contentURL}">
                                        <div><img src="${pageContext.request.contextPath}${post.contentURL}" alt="Post Media"></div>
                                    </c:if>
                                </div>
                            </a>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="no-results">No posts found.</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>
</div>

<!-- Dark Mode JavaScript -->
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const switchToggle = document.getElementById('themeSwitch');
        const body = document.body;
        const currentTheme = localStorage.getItem('theme');

        if (currentTheme === 'dark') {
            body.classList.add('dark-mode');
            if (switchToggle) switchToggle.checked = true;
        }

        if (switchToggle) {
            switchToggle.addEventListener('change', function () {
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