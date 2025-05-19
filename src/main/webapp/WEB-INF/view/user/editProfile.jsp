<%@ page import="aammo.ppv.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Profile</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/static/css/editProfile.css"></head>
<body>
<div class="edit-container">
    <div class="theme-toggle">
        <label class="switch">
            <input type="checkbox" id="darkModeToggle">
            <span class="slider"></span>
        </label>
    </div>
    <% User user = (User) session.getAttribute("user");
        if (user == null) { %>
    <h2>You are not logged in. Please <a href="<%= request.getContextPath() %>/register">register</a>.</h2>
    <% } else { %>
    <div class="edit-box">
        <h2>Edit Profile</h2>
        <form action="<%= request.getContextPath() %>/user/editProfile" method="POST">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" value="<%= user.getUsername() %>" required>
            </div>
            <div class="form-group">
                <label for="bio">Bio:</label>
                <textarea id="bio" name="bio" rows="4" cols="50"><%= user.getBio() %></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Save Changes</button>
            <button type="button" class="btn btn-secondary" onclick="location.href='<%= request.getContextPath() %>/user/profile'">Cancel</button>
        </form>
    </div>
    <% } %>
</div>
<script>
    const toggle = document.getElementById('darkModeToggle');
    if (localStorage.getItem('darkMode') === 'enabled') {
        document.body.classList.add('dark-mode');
        toggle.checked = true;
    }

    toggle.addEventListener('change', () => {
        if (toggle.checked) {
            document.body.classList.add('dark-mode');
            localStorage.setItem('darkMode', 'enabled');
        } else {
            document.body.classList.remove('dark-mode');
            localStorage.setItem('darkMode', 'disabled');
        }
    });
</script>
</body>
</html>