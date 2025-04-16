<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Registration.css"/>
    <title>User Registration Success</title>
</head>
<body>
<nav class="navbar w3-teal">
    <img src="${pageContext.request.contextPath}/images/Owl_logo.png" alt="Owl Logo" width="50" height="50">
    <a href="${pageContext.request.contextPath}/home" class="w3-button w3-teal w3-hover-pink">Home</a>
    <a href="${pageContext.request.contextPath}/login" class="w3-button w3-teal w3-hover-pink">Login</a>
    <a href="${pageContext.request.contextPath}/usersRegister" class="w3-button w3-teal w3-hover-pink">Sign up</a>
    <div class="w3-dropdown-hover w3-hover-pink">
        <button class="w3-button w3-teal w3-hover-pink ">Games</button>
        <div class="w3-dropdown-content w3-bar-block w3-card-4">
            <a href="#" class="w3-bar-item w3-button w3-pale-blue w3-hover-pink">Tic-Tac-Toe</a>
            <a href="#" class="w3-bar-item w3-button w3-pale-blue  w3-hover-pink">Guessing Game</a>
            <a href="#" class="w3-bar-item w3-button w3-pale-blue  w3-hover-pink">True/False</a>
            <a href="#" class="w3-bar-item w3-button w3-pale-blue w3-hover-pink">Memory Game</a>
        </div>
    </div>
</nav>
<div class="w3-panel w3-pale-green w3-leftbar w3-rightbar w3-border-green w3-display-middle">
    <div class="w3-center"><h1>Success!!!</h1></div>
    <div class="w3-center"><p>You have Registered</p></div>
</div>
</body>
<footer class="footer w3-teal w3-center">
    <p>Created by Owls Games - 2024</p>
</footer>
</html>