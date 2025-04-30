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
</head>
<body>
<h2>Hello there!</h2>
<h4>We are very happy to see you here!</h4>
<!-- Register Form-->
<form method="POST" action="UserRegister_Servlet.java">
    <label>User Name</label>
    <input type="text" name="userName" placeholder="Enter your user name" required>
    <br>
    <label>Email</label>
    <br>
    <input type="text" name="email" placeholder="Enter your email" required>
    <br>
    <label>Password</label>
    <br>
    <input type="password" name="password" placeholder="Enter your password" required>
    <br>
    <label>
    <input type="checkbox" required>
    By signing up, you are creating a ConPay account, and you agree to ConPay's
    <span>Terms of Use</span> and <span>Privacy Policy</span>.
    </label>

</form>

</body>
</html>
