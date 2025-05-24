<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<!-- This is an HTML comment -->
<html>
<head>
    <title>Register</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/register.css">
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
        <h2>Join ConPay</h2>
        <p class="subtitle">We're happy to see you here!</p>

        <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="error"><%= request.getAttribute("errorMessage") %></div>
        <% } %>

        <form action="register" method="post" onsubmit="return validateForm()">
            <div class="form-group">
                <label for="userName">Username</label>
                <input type="text" id="userName" name="userName" minlength="3" required>
            </div>

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$" required>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" minlength="8" required>
            </div>

            <div class="checkbox-group">
                <input type="checkbox" id="terms" name="terms" required>
                <label for="terms">
                    By signing up, you agree to our <a href="#">Terms</a> and <a href="#">Privacy Policy</a>.
                </label>
            </div>

            <button type="submit" class="btn-primary">Register</button>
        </form>

        <div class="bottom-link">
            <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Login</a></p>
        </div>
    </div>
</div>

<script>
    function validateForm() {
        const username = document.getElementById("userName").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        if (username.length < 3) {
            alert("Username must be at least 3 characters.");
            return false;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            alert("Email must end with @gmail.com");
            return false;
        }

        if (password.length < 8) {
            alert("Password must be at least 8 characters.");
            return false;
        }

        return true;
    }

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
