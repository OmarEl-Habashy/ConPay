<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body>
<div class="container">
    <div class="left">
        <p><strong>Hey!</strong> We are very happy to see you here!</p>
    </div>
    <!-- Already have account -->
    <div class="already-account">
        <p>You don't have an account yet?</p>
        <a href="${pageContext.request.contextPath}/register" class="btn btn-secondary">Register here!</a>
    </div>
    <div class="right">
        <div id = "error-message">
            <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error">
                <%= request.getAttribute("errorMessage") %>
            </div>
            <% } %>
        </div>

        <form action="login" method="post">
            <div class="form-group">
                <label for="username">User Name</label>
                <input type="text" name="username" id="username" placeholder="Your favourite UserName!" required>
            </div>

            <!-- Removed email field as per your request -->
            <!-- <div class="form-group">
                <label for="email">Email</label>
                <input type="email" name="email" id="email" placeholder="name@domain.com" required>
            </div> -->

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" name="password" id="password" placeholder="************" required>
            </div>

            <div class="checkbox-group">
                <input type="checkbox" name="agree" required>
                <span class="terms">
                    By signing up, you are creating a ConPay account, and you agree to ConPay's
                    <a href="#">Term of Use</a> and <a href="#">Privacy Policy</a>.
                </span>
            </div>

            <input type="submit" value="Login">
        </form>
    </div>
</div>
</body>
</html>
