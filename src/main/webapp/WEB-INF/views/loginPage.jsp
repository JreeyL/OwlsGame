<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login Page</title>
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/loginPage.css">
</head>
<body>

<%@ include file="userStatus.jspf" %>

<nav class="navbar w3-teal">
    <img src="${pageContext.request.contextPath}/images/Owl_logo.png" alt="Owl Logo" width="50" height="50">
    <a href="${pageContext.request.contextPath}/home" class="w3-button w3-teal w3-hover-pink">Home</a>
    <a href="${pageContext.request.contextPath}/login" class="w3-button w3-teal w3-hover-pink">Login</a>
    <a href="${pageContext.request.contextPath}/usersRegister" class="w3-button w3-teal w3-hover-pink">Sign up</a>
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
<form method="post" action="${pageContext.request.contextPath}/login">
    <div class="input-container">
        <input type="text" id="email" name="email" value="${userLoginDto.email}" required placeholder="Please enter your Email">
    </div>
    <div class="input-container">
        <input type="password" id="password" name="password" value="${userLoginDto.password}" required placeholder="Please enter your Password">
    </div>
    <div class="button-container">
        <button type="submit">Login</button>
        <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/reset-password'">Reset Password</button>
    </div>
</form>

<%-- Feedback message bar --%>
<%
    String message = (String) request.getAttribute("message");
    String messageType = (String) request.getAttribute("messageType"); // "success" or "failure"
    if (message != null && messageType != null) {
%>
<div class="login-msg-bar <%= messageType %>"><%= message %></div>
<%
    }
%>

<% if(request.getParameter("resetSuccess") != null) { %>
<div class="w3-panel w3-green w3-round-large w3-center">
    <p><%= request.getParameter("resetSuccess") %></p>
</div>
<% } %>

<footer class="footer w3-teal">
    <p>Owls games</p>
</footer>
</body>
</html>