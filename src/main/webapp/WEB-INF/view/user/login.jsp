<%@ page import="jakarta.servlet.http.Cookie" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body>
<div class="theme-toggle">
    <label class="switch">
        <input type="checkbox" id="themeSwitcher">
        <span class="slider round"></span>
    </label>
</div>

<div class="auth-container">
    <div class="auth-box">
        <h2>Welcome Back</h2>
        <p class="subtitle">Enter your credentials to continue</p>

        <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="error"><%= request.getAttribute("errorMessage") %></div>
        <% } %>

        <%
            String rememberedUsername = "";
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("username".equals(cookie.getName())) {
                        rememberedUsername = cookie.getValue();
                        break;
                    }
                }
            }
        %>

        <form action="login" method="post">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" value="<%= rememberedUsername %>" required>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>

            <button type="submit" class="btn-primary">Login</button>

            <div class="form-group" style="display: flex; align-items: center; gap: 8px;">
                <input type="checkbox" id="rememberMe" name="rememberMe" <%= rememberedUsername.isEmpty() ? "" : "checked" %>>
                <label for="rememberMe" style="margin: 0;">Remember Me</label>
            </div>
        </form>

        <div class="bottom-link">
            <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register</a></p>
        </div>
    </div>
</div>

<script>
    const toggle = document.getElementById("themeSwitcher");
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