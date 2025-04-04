<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link type="text/css" rel="stylesheet" href="/css/loginPage.css"> <!-- 更新路径 -->
<html>
<head>
    <title>Login Page_Owlsgame</title>
</head>
<body>

<nav class="navbar w3-teal">
    <a href="home" class="w3-button w3-teal w3-hover-pink">Home</a>
    <a href="login" class="w3-button w3-teal w3-hover-pink">Login</a>
    <a href="register" class="w3-button w3-teal w3-hover-pink">Sign up</a>
    <div class="w3-dropdown-hover w3-hover-pink">
        <button class="w3-button w3-teal w3-hover-pink">Games</button>
        <div class="w3-dropdown-content w3-bar-block w3-card-4">
            <a href="#" class="w3-bar-item w3-button w3-pale-blue w3-hover-pink">Tic-Tac-Toe</a>
            <a href="#" class="w3-bar-item w3-button w3-pale-blue w3-hover-pink">Guessing Game</a>
            <a href="#" class="w3-bar-item w3-button w3-pale-blue w3-hover-pink">True/False</a>
            <a href="#" class="w3-bar-item w3-button w3-pale-blue w3-hover-pink">Memory Game</a>
        </div>
    </div>
</nav>

<h2>Login</h2>
<form method="post" action="login">
    <div class="input-container">
        <input type="text" id="username" name="username" required placeholder="Please enter the Username">
    </div>
    <div class="input-container">
        <input type="password" id="password" name="password" required placeholder="Please enter the Password">
    </div>
    <div class="button-container">
        <button type="submit">Login</button>
        <button type="button" onclick="window.location.href='reset-password'">Reset Password</button>
    </div>
</form>

<%
    // 显示登录结果
    String message = (String) request.getAttribute("message");
    if (message != null) {
        String messageType = (String) request.getAttribute("messageType");
%>
<p class='<%= messageType %>'><%= message %></p>
<%
    }
%>
<footer class="footer w3-teal">
    <p>Owls games</p>
</footer>

</body>
</html>