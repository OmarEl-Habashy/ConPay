<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register User</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/register.css">
</head>
<body>
<div class="container">
    <h2>Hey!</h2>
    <h4>We are very happy to see you here!</h4>

    <!-- Already have account -->
    <div class="already-account">
        <p>Already have an account?</p>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Login here</a>
    </div>

    <!-- Register Form -->
    <form action="register" method="post">
        <div class="form-group">
            <label for="userName">User Name</label>
            <input type="text" id="userName" name="userName" placeholder="Your favourite UserName!" required>
        </div>

        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" placeholder="name@domain.com" required>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" placeholder="************" required>
        </div>

        <div class="checkbox-group terms">
            <input type="checkbox" id="terms" name="terms" required>
            <label for="terms">
                By signing up, you are creating a ConPay account, and
                you agree to ConPay's <a href="#">Term of Use</a> and <a href="#">Privacy Policy</a>.
            </label>
        </div>

        <button type="submit" class="btn btn-primary">Register</button>
    </form>
</div>
</body>
</html>
