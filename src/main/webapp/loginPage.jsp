<%@ page import="java.sql.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link type="text/css" rel="stylesheet" href="LoginPage.css">
<html>
<head>
    <title>Login Page</title>
</head>
<body>

<nav class="navbar w3-teal">
    <a href="Homepage.jsp" class="w3-button w3-teal w3-hover-pink">Home</a>
    <a href="LoginPage.jsp" class="w3-button w3-teal w3-hover-pink">Login</a>
    <a href="usersRegister.jsp" class="w3-button w3-teal w3-hover-pink">Sign up</a>
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
<form method="post" action="LoginPage.jsp">
    <div class="input-container">
        <input type="text" id="username" name="username" required placeholder="Please enter the Username">
    </div>
    <div class="input-container">
        <input type="password" id="password" name="password" required placeholder="Please enter the Password">
    </div>
    <div class="button-container">
        <button type="submit">Login</button>
        <button type="button" onclick="window.location.href='NewPwd.html'">Reset Password</button>
    </div>
</form>

<%
    response.setContentType("text/html;charset=UTF-8");
    request.setCharacterEncoding("UTF-8");

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    Integer attempts = (Integer) session.getAttribute("attempts");

    if (attempts == null) {
        attempts = 5; // 初始失败次数为5次
    }

    if (username != null && password != null) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/theusers?useUnicode=true&characterEncoding=utf8", "root", "RootRoot##");

            String sql = "SELECT * FROM users WHERE username = ? AND userpwd = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 登录成功，重置尝试次数
                session.setAttribute("attempts", 5);
                session.setAttribute("username", username);
                out.println("<p class='success'>Login successful! Redirecting to Home page...</p>");
                response.setHeader("Refresh", "1; URL= Homepage.jsp");
            } else {
                attempts--; // 失败次数减1
                session.setAttribute("attempts", attempts);

                if (attempts > 0) {
                    out.println("<p class='failure'>Invalid username or password. You have " + attempts + " attempts left.</p>");
                } else {
                    out.println("<p class='failure'>Too many failed attempts. Please try again later.</p>");
                    // 根据需求，可能会在这里禁用登录功能一定时间
                }
            }
        } catch (Exception e) {
            out.println("<p class='failure'>Error: " + e.getMessage() + "</p>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                out.println("<p class='failure'>Error closing database connection</p>");
            }
        }
    }
%>

<footer class="footer w3-teal">
    <p>Owls games</p>
</footer>

</body>
</html>
