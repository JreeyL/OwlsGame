let questions = [];
let currentQuestion = 0;
let score = 0;

const startView = document.getElementById("start-view");
const gameView = document.getElementById("game-view");
const gameOverView = document.getElementById("game-over");
const questionEl = document.getElementById("question");
const resultEl = document.getElementById("result");
const finalScoreEl = document.getElementById("final-score");

// 启动游戏，先拉取题目
function startGame() {
    currentQuestion = 0;
    score = 0;
    finalScoreEl.textContent = "";
    startView.classList.add("hidden");
    gameOverView.classList.add("hidden");
    gameView.classList.remove("hidden");

    fetch('/api/tof/questions?num=10') // 可调整题目数量
        .then(res => res.json())
        .then(data => {
            questions = data;
            showQuestion();
        })
        .catch(err => {
            questionEl.textContent = "Failed to load questions.";
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
    gameView.classList.add("hidden");
    gameOverView.classList.remove("hidden");
    finalScoreEl.textContent = `Your score: ${score} / ${questions.length}`;
}

function restartGame() {
    startGame();
}