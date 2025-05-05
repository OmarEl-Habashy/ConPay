<%@ page import="aammo.ppv.model.User" %><%--
  Created by IntelliJ IDEA.
  User: omare
  Date: 5/2/2025
  Time: 8:19 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Profile</title>
</head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/editProfile.css">
<body>

<% User user = (User) session.getAttribute("user");
   if (user == null) { %>
    <h2>You are not logged in. Please <a href="${pageContext.request.contextPath}/register">register</a>.</h2>
<% } else { %>
<h2>Edit Profile</h2>

<form action="${pageContext.request.contextPath}/user/editProfile" method="POST">
    <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" value="<%= user.getUsername() %>" required>
    </div>
    <div class="form-group">
        <label for="bio">Bio:</label>
        <textarea id="bio" name="bio" rows="4" cols="50"><%= user.getBio() %></textarea>
    </div>
    <button type="submit" class="btn btn-primary">Save Changes</button>
    <button type="button" class="btn btn-secondary" onclick="location.href='${pageContext.request.contextPath}/user/profile'">Cancel</button>
</form>
<% } %>
</body>
</html>
