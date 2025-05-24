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

    String profileUsername = "";

    if (post != null && post.getUsername() != null) {
        profileUsername = post.getUsername();
    }
%>

<a href="${pageContext.request.contextPath}/user/profile/<%=profileUsername%>" class="back-link">← Return to Profile</a>
<div class = "post-container">
    <div class="post">


        <!-- Post Image -->
        <% if (post.getContentURL() != null && !post.getContentURL().isEmpty()) { %>
        <div class="post-image">
            <img src="${pageContext.request.contextPath}${post.getContentURL()}" alt="Post content">
        </div>
        <% } %>

        <!-- Like Section -->
        <div class="post-actions">
            <form action="${pageContext.request.contextPath}/viewPost" method="post" class="like-form">
                <input type="hidden" name="action" value="like">
                <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                <button type="submit" class="like-btn">
                    ❤ <%= likeCount %> likes
                </button>
            </form>
        </div>

        <!-- Caption -->
        <div class="post-caption">
            <span class="caption"><%= post.getCaption() %></span>
        </div>

        <!-- Comments Section -->
        <div class="comments-section">
            <h3>Comments (<%= comments.size() %>)</h3>
            <% for (Comment comment : comments) { %>
            <div class="comment">
                <span class="comment-text"><%= comment.getContent() %></span>
                <span class="comment-date"><%= comment.getCreatedAt() %></span>
            </div>
            <% } %>

            <!-- Comment Form -->
            <form action="${pageContext.request.contextPath}/viewPost" method="post" class="comment-form">
                <input type="hidden" name="action" value="comment">
                <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                <textarea name="content" placeholder="Add a comment..." required></textarea>
                <button type="submit">Post</button>
            </form>
        </div>
    </div>
</div>
</body>
</html>