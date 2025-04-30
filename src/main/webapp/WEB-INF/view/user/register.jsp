<%--
  Created by IntelliJ IDEA.
  User: omare
  Date: 4/30/2025
  Time: 6:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register User</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/register.css">
</head>
<body>
<h2>Hey!</h2>
<h4>We are very happy to see you here!</h4>
<!-- Register Form-->
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

    <button type="submit" class="btn btn-primary">Login</button>
</form>
</body>
</html>
