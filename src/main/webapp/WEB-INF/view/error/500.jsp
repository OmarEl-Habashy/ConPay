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
    <title>Server Error</title>
    <style>
        .error-container {
            text-align: center;
            padding: 50px 20px;
            max-width: 800px;
            margin: 0 auto;
            font-family: Arial, sans-serif;
        }
        .error-image {
            max-width: 300px;
            margin-bottom: 30px;
        }
        .error-title {
            font-size: 24px;
            margin-bottom: 15px;
            color: #e47911;
        }
        .error-message {
            font-size: 16px;
            color: #333;
            margin-bottom: 20px;
        }
        .home-link {
            display: inline-block;
            padding: 10px 20px;
            background-color: lightblue;
            border: 1px solid #a88734;
            border-radius: 3px;
            color: #111;
            text-decoration: none;
            font-weight: bold;
        }
        .home-link:hover {
            background-color: darkcyan;
        }
    </style>
</head>
<body>
<div class="error-container">
    <img src="${pageContext.request.contextPath}/static/images/Dog.jpg" alt="Cute Dog" class="error-image">
    <h1 class="error-title">Oops! Something Went Wrong</h1>
    <p class="error-message">Sorry, this looks like a small problem on our end. We're working on it!</p>
    <p class="error-message">Our team has been notified and we're fixing the issue.</p>
    <a href="${pageContext.request.contextPath}/feed" class="home-link">Go to Feed</a>
</div>
</body>
</html>
