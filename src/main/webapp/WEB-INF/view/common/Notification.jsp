<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>Notifications | PPV</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/Notification.css">
</head>
<body>
<header class="navbar navbar-expand-lg sticky-top">
    <div class="container">
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
                            <c:forEach var="notification" items="${notifications}" end="4">
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
                    <div class="profile-pic-small me-2">${user.username.substring(0,1).toLowerCase()}</div>
                    ${user.username}
                </button>
                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">Profile</a></li>
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                </ul>
            </div>
        </div>
    </div>
</header>

<div class="container mt-4">
    <div class="row">
        <div class="col-lg-8 mx-auto">
            <a href="${pageContext.request.contextPath}/feed" class="btn btn-outline-primary btn-back">
                <i class="bi bi-arrow-left"></i> Back to Feed
            </a>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <h1>Notifications</h1>
                <c:if test="${not empty notifications}">
                    <a href="${pageContext.request.contextPath}/notifications?action=markAllRead" class="btn btn-sm btn-outline-secondary">
                        Mark all as read
                    </a>
                </c:if>
            </div>

            <c:choose>
                <c:when test="${empty notifications}">
                    <div class="empty-notifications">
                        <i class="bi bi-bell" style="font-size: 3rem;"></i>
                        <h4 class="mt-3">No notifications yet</h4>
                        <p>When you get notifications, they'll show up here.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="list-group">
                        <c:forEach var="notification" items="${notifications}">
                            <div class="list-group-item notification-item ${notification.read ? '' : 'notification-unread'}">
                                <div class="d-flex">
                                    <div class="notification-avatar me-3">
                                            ${notification.senderUsername.substring(0,1).toLowerCase()}
                                    </div>
                                    <div class="notification-content">
                                        <div class="d-flex align-items-center mb-1">
                                            <c:choose>
                                                <c:when test="${notification.type == 'FOLLOW'}">
                                                    <i class="bi bi-person-plus-fill notification-type-icon notification-type-follow"></i>
                                                </c:when>
                                                <c:when test="${notification.type == 'LIKE'}">
                                                    <i class="bi bi-heart-fill notification-type-icon notification-type-like"></i>
                                                </c:when>
                                                <c:when test="${notification.type == 'COMMENT'}">
                                                    <i class="bi bi-chat-fill notification-type-icon notification-type-comment"></i>
                                                </c:when>
                                                <c:when test="${notification.type == 'MENTION'}">
                                                    <i class="bi bi-at notification-type-icon notification-type-mention"></i>
                                                </c:when>
                                            </c:choose>
                                            <p class="mb-0">${notification.content}</p>
                                        </div>
                                        <div class="notification-time">${notification.formattedTime}</div>

                                        <div class="notification-actions">
                                            <c:if test="${not notification.read}">
                                                <a href="${pageContext.request.contextPath}/notifications?action=markRead&id=${notification.notificationId}" class="btn btn-sm btn-outline-secondary">
                                                    Mark as read
                                                </a>
                                            </c:if>

                                            <c:choose>
                                                <c:when test="${notification.type == 'FOLLOW'}">
                                                    <a href="${pageContext.request.contextPath}/user/profile?id=${notification.senderId}" class="btn btn-sm btn-outline-primary">
                                                        View Profile
                                                    </a>
                                                </c:when>
                                                <c:when test="${notification.type == 'LIKE' || notification.type == 'COMMENT' || notification.type == 'MENTION'}">
                                                    <a href="${pageContext.request.contextPath}/viewPost?postId=${notification.referenceId}" class="btn btn-sm btn-outline-primary">
                                                        View Post
                                                    </a>
                                                </c:when>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<div class="theme-toggle">
    <label class="switch">
        <input type="checkbox" id="themeSwitch">
        <span class="slider round"></span>
    </label>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
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
</body>
</html>