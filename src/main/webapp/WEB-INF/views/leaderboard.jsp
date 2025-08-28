<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Leaderboard - Owls Games</title>
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
        .leaderboard-item {
            margin-bottom: 15px;
            border-radius: 8px;
            transition: transform 0.2s;
        }
        .leaderboard-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }
        .rank-badge {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            color: white;
        }
        .rank-1 { background: linear-gradient(45deg, #FFD700, #FFA500); }
        .rank-2 { background: linear-gradient(45deg, #C0C0C0, #A0A0A0); }
        .rank-3 { background: linear-gradient(45deg, #CD7F32, #8B4513); }
        .rank-other { background: linear-gradient(45deg, #4CAF50, #45a049); }
        .score-bar {
            background: linear-gradient(90deg, #e3f2fd, #1976d2);
            border-radius: 20px;
            padding: 8px 15px;
            color: white;
            font-weight: bold;
        }
        .player-name {
            font-size: 18px;
            font-weight: bold;
            color: #1976d2;
        }
        .game-selector {
            margin-bottom: 30px;
        }
        .crown-icon {
            color: #FFD700;
            margin-right: 10px;
        }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="w3-sidebar w3-bar-block w3-pale-blue" style="width: 25%">
    <h3 class="w3-bar-item"><i class="fas fa-trophy crown-icon"></i>Leaderboard</h3>
    <a href="/leaderboard?game=True/False" class="w3-bar-item w3-button w3-teal w3-hover-pink">
        <i class="fas fa-question-circle"></i> True/False
    </a>
    <p class="w3-margin">View the top performers in our exciting True or False facts game!</p>
    
    <a href="/leaderboard?game=Tic-Tac-Toe" class="w3-bar-item w3-button w3-teal w3-hover-pink">
        <i class="fas fa-th"></i> Tic-Tac-Toe
    </a>
    <p class="w3-margin">Check out the masters of strategic thinking in Tic-Tac-Toe!</p>
    
    <a href="/leaderboard?game=Memory Game" class="w3-bar-item w3-button w3-teal w3-hover-pink">
        <i class="fas fa-brain"></i> Memory Game
    </a>
    <p class="w3-margin">Discover who has the sharpest memory skills!</p>
    
    <a href="/leaderboard?game=Guessing Game" class="w3-bar-item w3-button w3-teal w3-hover-pink">
        <i class="fas fa-search"></i> Guessing Game
    </a>
    <p class="w3-margin">See who are the best number guessers in our community!</p>
</div>

<!-- Main Content -->
<div style="margin-left: 25%">
    <!-- Navigation Bar -->
    <nav class="navbar w3-teal">
        <img src="/images/Owl_logo.png" alt="Owl Logo" width="50" height="50" />
        <a href="/home" class="w3-button w3-teal w3-hover-pink">Home</a>
        <a href="/login" class="w3-button w3-teal w3-hover-pink">Login</a>
        <a href="/usersRegister" class="w3-button w3-teal w3-hover-pink">Sign up</a>
        <a href="/leaderboard" class="w3-button w3-teal w3-hover-pink w3-blue">Leaderboard</a>
    </nav>

    <!-- Header Section -->
    <div class="w3-container w3-white w3-center w3-opacity-min">
        <h1><i class="fas fa-trophy crown-icon"></i>Leaderboard</h1>
        <p>Celebrating our top performers!</p>
    </div>

    <!-- Game Selector -->
    <div class="w3-container w3-margin-top game-selector">
        <div class="w3-card w3-padding w3-white">
            <h3><i class="fas fa-gamepad"></i> Currently Viewing: ${currentGame}</h3>
            <p class="w3-text-grey">Top 5 players with highest scores</p>
        </div>
    </div>

    <!-- Leaderboard -->
    <div class="w3-container w3-margin-top">
        <%
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> topPlayers = (List<Map<String, Object>>) request.getAttribute("topPlayers");
            if (topPlayers != null && !topPlayers.isEmpty()) {
                for (int i = 0; i < topPlayers.size(); i++) {
                    Map<String, Object> player = topPlayers.get(i);
                    String firstName = (String) player.get("firstName");
                    String lastName = (String) player.get("lastName");
                    String email = (String) player.get("email");
                    Integer scoreValue = (Integer) player.get("scoreValue");
                    Integer playTime = (Integer) player.get("playTime");
                    
                    String displayName = "";
                    if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
                        displayName = firstName + " " + lastName;
                    } else {
                        displayName = email != null ? email : "Unknown Player";
                    }
                    
                    String rankClass = "";
                    String rankIcon = "";
                    if (i == 0) {
                        rankClass = "rank-1";
                        rankIcon = "fas fa-crown";
                    } else if (i == 1) {
                        rankClass = "rank-2"; 
                        rankIcon = "fas fa-medal";
                    } else if (i == 2) {
                        rankClass = "rank-3";
                        rankIcon = "fas fa-award";
                    } else {
                        rankClass = "rank-other";
                        rankIcon = "";
                    }
        %>
        <div class="leaderboard-item w3-card w3-padding w3-white">
            <div class="w3-row">
                <!-- Rank Badge -->
                <div class="w3-col s1 w3-center">
                    <div class="rank-badge <%= rankClass %>">
                        <% if (!rankIcon.isEmpty()) { %>
                            <i class="<%= rankIcon %>"></i>
                        <% } else { %>
                            <%= i + 1 %>
                        <% } %>
                    </div>
                </div>
                
                <!-- Player Info -->
                <div class="w3-col s6">
                    <div class="player-name"><%= displayName %></div>
                    <div class="w3-text-grey w3-small">
                        <i class="fas fa-envelope"></i> <%= email != null ? email : "N/A" %>
                    </div>
                    <div class="w3-text-grey w3-small">
                        <i class="fas fa-clock"></i> Play Time: <%= playTime != null ? playTime : 0 %> seconds
                    </div>
                </div>
                
                <!-- Score Display -->
                <div class="w3-col s5">
                    <div class="score-bar w3-center">
                        <span style="font-size: 24px;"><%= scoreValue != null ? scoreValue : 0 %></span>
                        <span style="font-size: 14px;"> points</span>
                    </div>
                </div>
            </div>
        </div>
        <%
                }
            } else {
        %>
        <!-- No Players Message -->
        <div class="w3-card w3-padding w3-white w3-center">
            <div style="padding: 40px;">
                <i class="fas fa-trophy" style="font-size: 64px; color: #ccc;"></i>
                <h3>No Scores Yet!</h3>
                <p class="w3-text-grey">Be the first to play ${currentGame} and claim your spot on the leaderboard!</p>
                <a href="/TofGame.html" class="w3-button w3-teal w3-hover-pink w3-margin-top">
                    <i class="fas fa-play"></i> Play Now
                </a>
            </div>
        </div>
        <%
            }
        %>
    </div>

    <!-- Call to Action -->
    <div class="w3-container w3-margin-top">
        <div class="w3-card w3-pale-blue w3-padding w3-center">
            <h4><i class="fas fa-star"></i> Think you can make it to the top?</h4>
            <p>Challenge yourself and see if you have what it takes to join our elite players!</p>
            <div class="w3-row w3-margin-top">
                <div class="w3-col s3">
                    <a href="/TofGame.html" class="w3-button w3-block w3-teal w3-hover-pink">
                        <i class="fas fa-question-circle"></i><br>True/False
                    </a>
                </div>
                <div class="w3-col s3">
                    <a href="#" class="w3-button w3-block w3-teal w3-hover-pink">
                        <i class="fas fa-th"></i><br>Tic-Tac-Toe
                    </a>
                </div>
                <div class="w3-col s3">
                    <a href="#" class="w3-button w3-block w3-teal w3-hover-pink">
                        <i class="fas fa-brain"></i><br>Memory Game
                    </a>
                </div>
                <div class="w3-col s3">
                    <a href="#" class="w3-button w3-block w3-teal w3-hover-pink">
                        <i class="fas fa-search"></i><br>Guessing Game
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Back to Home -->
    <div class="w3-container w3-margin-top w3-margin-bottom">
        <a href="/home" class="w3-button w3-green w3-hover-light-green">
            <i class="fas fa-home"></i> Back to Home
        </a>
    </div>
</div>

<!-- Footer -->
<footer class="footer w3-teal w3-center" style="margin-left: 10%">
    <p>Created by Owls Games - 2025</p>
</footer>

</body>
</html>
