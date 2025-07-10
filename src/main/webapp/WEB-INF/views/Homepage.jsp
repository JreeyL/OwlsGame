<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css" />
    <link rel="stylesheet" href="/css/Homepage.css" />
</head>
<body>
<div class="w3-sidebar w3-bar-block w3-pale-blue" style="width: 25%">
    <h3 class="w3-bar-item">Games</h3>
    <a href="#" class="w3-bar-item w3-button w3-teal w3-hover-pink">Tic-Tac-Toe</a>
    <p class="w3-margin">Tic-Tac-Toe is a fast game where two players take turns marking a 3x3 grid with Xs and Os. The goal is to get three in a row before your opponent!</p>
    <a href="#" class="w3-bar-item w3-button w3-teal w3-hover-pink">Guessing Game</a>
    <p class="w3-margin">In a number guessing game, one player thinks of a number, and the other has to guess it! After each guess, the first player says if the number is higher or lower. Keep guessing until you crack the code</p>

    <a href="/TofGame.html" class="w3-bar-item w3-button w3-teal w3-hover-pink">True/False</a>
    <p class="w3-margin">In a True or False facts game, players take turns sharing statements, and the others have to guess if they are true or false. It's a fun way to test your knowledge and learn new things!</p>
    <a href="#" class="w3-bar-item w3-button w3-teal w3-hover-pink">Memory Game</a>
    <p class="w3-margin">In a price item matching game, players match products to their correct prices. Guess the right price, and earn points! It's a fun way to test your knowledge of how much things cost.</p>
</div>

<div style="margin-left: 25%">
    <nav class="navbar w3-teal">
        <img src="/images/Owl_logo.png" alt="Owl Logo" width="50" height="50" />
        <a href="/home" class="w3-button w3-teal w3-hover-pink">Home</a>
        <a href="/login" class="w3-button w3-teal w3-hover-pink">Login</a>
        <a href="/usersRegister" class="w3-button w3-teal w3-hover-pink">Sign up</a>
        <a href="/leaderboard" class="w3-button w3-teal w3-hover-pink">Leaderboard</a>
    </nav>

    <div class="w3-container w3-white w3-center w3-opacity-min">
        <h1>Welcome to Owls Games</h1>
    </div>
    <div class="w3-container w3-white w3-center w3-opacity-min">
        <p>Your ultimate nest for all things gaming! Whether you're a casual player or a hardcore enthusiast, we have got a cozy spot here just for you to store, organize, and revisit your favorite games. With a sleek and easy-to-use interface, you can quickly add the titles that make you smile, share your top picks with friends, and never forget the games that have your heart. So sit back, relax, and let Owls Games be your go-to hub for gaming memories. Your adventure starts here!</p>
    </div>
    <div class="w3-container w3-white w3-center w3-opacity-min">
        <h2>Favourite Games</h2>
    </div>
    <br>

    <div id="cards" class="w3-container">
        <div class="w3-cell-row">
            <div class="w3-container w3-cell">
                <img src="/images/Tic-Tac-Toe_Image.png" alt="Tic-Tac-Toe" style="width: 102%">
                <button class="w3-button w3-block w3-teal w3-hover-pink" style="width: 102%">Tic-Tac-Toe</button>
            </div>
            <div class="w3-container w3-cell">
                <img src="/images/Guessing_Game_Image.png" alt="Guessing Game" style="width: 94%">
                <button class="w3-button w3-block w3-teal w3-hover-pink" style="width: 94%">Guessing Game</button>
            </div>
            <div class="w3-container w3-cell">
                <img src="/images/True_Or_False_Game.png" alt="True/False" style="width: 105%">

                <a href="/TofGame.html" class="w3-button w3-block w3-teal w3-hover-pink" style="width: 105%; display: inline-block; text-align: center;">True/False</a>

            </div>
            <div class="w3-container w3-cell">
                <img src="/images/Memory_Game_Image.png" alt="Memory Game" style="width: 98%">
                <button class="w3-button w3-block w3-teal w3-hover-pink" style="width: 98%">Memory Game</button>
            </div>
            <br>
        </div>
    </div>
</div>
</body>
<footer class="footer w3-teal w3-center" style="margin-left: 10%">
    <p>Created by Owls Games - 2024</p>
</footer>
</html>