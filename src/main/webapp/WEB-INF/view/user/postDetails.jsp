<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="aammo.ppv.model.Post" %>
<%@ page import="aammo.ppv.model.User" %>
<%@ page import="aammo.ppv.model.Comment" %>
<%@ page import="java.util.List" %>

<html>
<head>
    <title>View Post</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/post-details.css">
</head>
<body>
<%
    Post post = (Post) request.getAttribute("post");
    List<Comment> comments = (List<Comment>) request.getAttribute("comments");
    Integer likeCount = (Integer) request.getAttribute("likeCount");
    String source = request.getParameter("source");

    String profileUsername = "";
    String backLink = "";
    String backText = "";

    if (post != null && post.getUsername() != null) {
        profileUsername = post.getUsername();
    }

    if ("feed".equals(source)) {
        backLink = request.getContextPath() + "/feed";
        backText = "← Return to Feed";
    } else {
        backLink = request.getContextPath() + "/user/profile/" + profileUsername;
        backText = "← Return to Profile";
    }
%>

<a href="<%= backLink %>" class="back-link"><%= backText %></a>
<div class="post-container">
    <div class="post">
        <div class="post-header">
            <a href="${pageContext.request.contextPath}/user/profile/${post.username}" class="post-username-link">
                @${post.username}
            </a>
        </div>

        <% if (post.getContentURL() != null && !post.getContentURL().isEmpty()) { %>
        <div class="post-media">
            <% if (post.getContentURL().startsWith("http://") || post.getContentURL().startsWith("https://")) { %>
            <img src="<%= post.getContentURL() %>" alt="Post content" style="max-width: 600px; align-content: center;">
            <% } else { %>
            <img src="${pageContext.request.contextPath}${post.getContentURL()}" alt="Post content" style="max-width: 600px; align-content: center;">
            <% } %>
        </div>
        <% } %>

        <div class="post-actions">
            <form action="${pageContext.request.contextPath}/viewPost" method="post" class="like-form">
                <input type="hidden" name="action" value="like">
                <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                <input type="hidden" name="source" value="<%= source %>">
                <button type="submit" class="like-btn">
                    ❤ <%= likeCount %> likes
                </button>
            </form>
        </div>

        <div class="post-caption">
            <span class="caption"><%= post.getCaption() %></span>
        </div>

        <div class="comments-section">
            <h3>Comments (<%= comments.size() %>)</h3>
            <% for (Comment comment : comments) { %>
            <div class="comment">
                <div class="comment-header">
                    <div class="comment-user-content">
                        <div class="comment-user-info">
                            <a class="comment-username-link" href="${pageContext.request.contextPath}/user/profile/<%= comment.getUsername() %>"><%= comment.getUsername() %></a>
                            <span class="comment-date"><%= comment.getCreatedAt() %></span>
                        </div>
                        <span class="comment-text"><%= comment.getContent() %></span>
                    </div>
                </div>
            </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/viewPost" method="post" class="comment-form">
                <input type="hidden" name="action" value="comment">
                <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                <input type="hidden" name="source" value="<%= source %>">
                <textarea name="content" placeholder="Add a comment..." required></textarea>
                <button type="submit">Post</button>
            </form>
        </div>
    </div>
</div>
</body>
</html>