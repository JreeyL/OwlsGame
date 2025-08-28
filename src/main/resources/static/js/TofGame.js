let questions = [];
let currentQuestion = 0;
let score = 0;
let gameStartTime;
let sessionInfo = null;
let isUserLoggedIn = false;
let userEmail = null;

const startView = document.getElementById("start-view");
const gameView = document.getElementById("game-view");
const gameOverView = document.getElementById("game-over");
const questionEl = document.getElementById("question");
const resultEl = document.getElementById("result");
const finalScoreEl = document.getElementById("final-score");

// 启动游戏，先拉取题目
function startGame() {
    console.log("Starting game...");
    currentQuestion = 0;
    score = 0;
    gameStartTime = new Date();
    finalScoreEl.textContent = "";
    startView.classList.add("hidden");
    gameOverView.classList.add("hidden");
    gameView.classList.remove("hidden");

    // 获取会话信息
    getSessionInfo();

    fetch('/api/tof/questions?num=5') // 使用5个题目便于测试
        .then(res => res.json())
        .then(data => {
            questions = data;
            console.log("Loaded questions:", questions.length);
            showQuestion();
        })
        .catch(err => {
            questionEl.textContent = "Failed to load questions.";
            console.error("Error loading questions:", err);
        });
}

// 获取会话信息并检查登录状态
function getSessionInfo() {
    console.log("Fetching session information...");
    fetch('/api/session/current')
        .then(response => response.json())
        .then(data => {
            console.log("Session data received:", data);
            sessionInfo = data;

            // 确定用户是否已登录
            isUserLoggedIn = data.isLoggedIn === true || Boolean(data.email);
            userEmail = data.email || null;

            if (isUserLoggedIn) {
                console.log("User is logged in as:", userEmail);
            } else {
                console.log("User is not logged in");
            }
        })
        .catch(err => {
            console.error("Error fetching session:", err);
            isUserLoggedIn = false;
            userEmail = null;
        });
}

function showQuestion() {
    resultEl.classList.add("hidden");
    resultEl.textContent = "";
    if (currentQuestion < questions.length) {
        questionEl.textContent = questions[currentQuestion].content;
    }
}

function submitAnswer(isTrue) {
    const correct = questions[currentQuestion].answer === isTrue;
    resultEl.textContent = correct ? "Correct!" : "Incorrect!";
    resultEl.className = correct ? "correct" : "incorrect";
    resultEl.classList.remove("hidden");
    if (correct) score++;

    setTimeout(() => {
        currentQuestion++;
        if (currentQuestion < questions.length) {
            showQuestion();
        } else {
            endGame();
        }
    }, 800);
}

function endGame() {
    console.log("Game ended. Score:", score, "/", questions.length);
    gameView.classList.add("hidden");
    gameOverView.classList.remove("hidden");

    // 计算游戏时长（秒）
    const gameEndTime = new Date();
    const playTime = Math.round((gameEndTime - gameStartTime) / 1000);

    finalScoreEl.textContent = `Your score: ${score} / ${questions.length}`;

    // 刷新会话信息
    fetch('/api/session/current')
        .then(response => response.json())
        .then(data => {
            console.log("Final session check:", data);

            // 明确检查登录状态
            isUserLoggedIn = data.isLoggedIn === true || Boolean(data.email);
            userEmail = data.email || userEmail;

            if (isUserLoggedIn && userEmail) {
                console.log("User confirmed logged in as:", userEmail);

                const userData = {
                    email: userEmail,
                    userId: data.userId || 1
                };

                saveScore(userData, score, playTime);
            } else {
                console.log("User not logged in for score saving");
                finalScoreEl.innerHTML += "<br><small>Log in to save your score!</small>";
            }
        })
        .catch(error => {
            console.error("Error checking session:", error);
            finalScoreEl.innerHTML += "<br><small>Error checking login status.</small>";

        });
}

function saveScore(user, finalScore, playTime) {
    console.log("Saving score:", {user, finalScore, playTime});

    const payload = {
        username: user.email,
        score: finalScore,
        playTime: playTime,
        userId: user.userId
    };

    console.log("Sending payload:", payload);

    fetch('/api/tof/score', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => response.json())
        .then(data => {
            console.log("Score save response:", data);
            if (data.success) {
                finalScoreEl.innerHTML += "<br><span style='color:green'>Score saved successfully!</span>";
                // 刷新会话信息获取更新后的分数
                getSessionInfo();
            } else {
                finalScoreEl.innerHTML += "<br><span style='color:red'>Failed to save score: " + (data.error || "unknown error") + "</span>";

                // 如果保存失败，提供重试选项
                finalScoreEl.innerHTML += `
                <br><button id="retry-save" 
                style="margin-top:10px;padding:5px 10px;background:#2196F3;color:white;border:none;border-radius:4px;">
                Retry Save</button>`;

                document.getElementById('retry-save').addEventListener('click', function() {
                    saveScore(user, finalScore, playTime);
                });
            }
        })
        .catch(error => {
            console.error('Error saving score:', error);
            finalScoreEl.innerHTML += "<br><span style='color:red'>Error: " + error.message + "</span>";

            // 网络错误时也提供重试
            finalScoreEl.innerHTML += `
            <br><button id="retry-save" 
            style="margin-top:10px;padding:5px 10px;background:#2196F3;color:white;border:none;border-radius:4px;">
            Retry Save</button>`;

            document.getElementById('retry-save').addEventListener('click', function() {
                saveScore(user, finalScore, playTime);
            });
        });
}

function restartGame() {
    startGame();
}