<%--
  Created by IntelliJ IDEA.
  User: omare
  Date: 4/30/2025
  Time: 9:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Page Not Found</title>
</head>
<body>
    <div class = "error-container">
        <img src = "${pageContext.request.contextPath}/static/images/Dog.jpg" alt="Cute Dog" class="error-image">
        <h1 class="error-title">Oops! Page Not Found</h1>
        <p class="error-message">Sorry, this looks like a small problem on our end. We're working on it!</p>
        <p class="error-message">The page you're looking for might have been moved or doesn't exist.</p>
        <a href="${pageContext.request.contextPath}/feed" class="home-link">Go to Feed</a>
    </div>

</body>
</html>
