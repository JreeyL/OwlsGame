<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Registration.css" />
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
<% if (request.getAttribute("error") != null) { %>
<div style="color:red;"><%= request.getAttribute("error") %></div>
<% } %>
<form action="${pageContext.request.contextPath}/register" method="post">
    <div class="container">
        <h1>Register</h1>
        <p>Please fill in this form to create an account.</p>
        <br>
        <label for="firstname"> First Name: </label> <br>
        <input type="text" id="firstname" name="firstname" class="w3-round-large" placeholder="Insert your first name" required> <br>
        <label for="lastname"> Last Name: </label> <br>
        <input type="text" id="lastname" name="lastname" class="w3-round-large" placeholder="Insert your last name" required> <br>
        <label for="email">Email: </label> <br>
        <input type="email" id="email" name="email" class="w3-round-large" placeholder="Insert your email" required> <br>
        <label for="password"> Password: </label> <br>
        <input type="password" id="password" name="password" class="w3-round-large" placeholder="Insert your password"
               pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"
               title="Must contain at least one number and one uppercase and lowercase letter, and must be least 8 characters"
               required> <br>
        <button type="submit" class="w3-button w3-teal w3-hover-pink">Register</button>
        <br>
    </div>
    <div class="container signin">
        <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Login</a>.</p>
    </div>
</form>
</body>
<footer class="footer w3-teal  w3-center">
    <p>Created by Owls Games - 2024</p>
</footer>
</html>