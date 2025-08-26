/**
 * Admin Games Management Script
 * Last Updated: 2025-08-24
 */
document.addEventListener('DOMContentLoaded', function() {
    // DOM元素
    const gamesContainer = document.getElementById('games-container');
    const addGameBtn = document.getElementById('add-game-btn');
    const addGameForm = document.getElementById('add-game-form');
    const gameForm = document.getElementById('game-form');
    const cancelBtn = document.getElementById('cancel-btn');
    const confirmModal = document.getElementById('confirm-modal');
    const confirmMessage = document.getElementById('confirm-message');
    const confirmYesBtn = document.getElementById('confirm-yes');
    const confirmNoBtn = document.getElementById('confirm-no');
    const statsContainer = document.getElementById('stats-container');

    // 初始化
    initializePage();

    function initializePage() {
        // 加载系统统计数据
        if (statsContainer) {
            loadSystemStats();
        }

        // 加载所有游戏
        loadGames();

        // 为添加游戏按钮添加事件监听器
        if (addGameBtn) {
            addGameBtn.addEventListener('click', function() {
                addGameForm.style.display = 'block';
            });
        }

        // 为取消按钮添加事件监听器
        if (cancelBtn) {
            cancelBtn.addEventListener('click', function() {
                addGameForm.style.display = 'none';
                gameForm.reset();
            });
        }

        // 为游戏表单添加提交事件监听器
        if (gameForm) {
            gameForm.addEventListener('submit', handleGameFormSubmit);
        }

        // 为确认对话框中的"否"按钮添加事件监听器
        if (confirmNoBtn) {
            confirmNoBtn.addEventListener('click', function() {
                confirmModal.style.display = 'none';
            });
        }

        // 点击对话框外部关闭对话框
        window.addEventListener('click', function(event) {
            if (event.target === confirmModal) {
                confirmModal.style.display = 'none';
            }
        });
    }

    // 加载系统统计数据
    function loadSystemStats() {
        fetch('/api/admin/stats')
            .then(response => {
                if (!response.ok) throw new Error('Failed to load system statistics');
                return response.json();
            })
            .then(stats => {
                statsContainer.innerHTML = `
                    <div class="w3-panel w3-round w3-pale-blue w3-padding">
                        <h4><i class="w3-margin-right fas fa-chart-bar"></i>System Overview</h4>
                        <div class="w3-row">
                            <div class="w3-col s6 m6 l6">
                                <p><b>Total Users:</b> ${stats.totalUsers || 0}</p>
                                <p><b>Total Games:</b> ${stats.totalGames || 0}</p>
                            </div>
                            <div class="w3-col s6 m6 l6">
                                <p><b>Total Scores:</b> ${stats.totalScores || 0}</p>
                                <p><b>Average Score:</b> ${stats.averageScore || 0}</p>
                            </div>
                        </div>
                        <p class="w3-small w3-right-align">Last updated: ${new Date().toLocaleString()}</p>
                    </div>
                `;
            })
            .catch(error => {
                console.error('Error loading stats:', error);
                statsContainer.innerHTML = `
                    <div class="w3-panel w3-pale-red">
                        <p>Failed to load system statistics: ${error.message}</p>
                    </div>
                `;
            });
    }

    // 处理游戏表单提交
    function handleGameFormSubmit(e) {
        e.preventDefault();

        const gameName = document.getElementById('game-name').value;
        const maxScore = document.getElementById('max-score').value;

        if (!gameName || !maxScore) {
            showMessage('Please fill in all required fields', 'error');
            return;
        }

        const gameData = {
            name: gameName,
            maxScore: parseInt(maxScore)
        };

        // 显示加载指示器
        const submitBtn = gameForm.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Saving...';

        fetch('/api/admin/games', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(gameData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server returned ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(game => {
                // 重置表单并隐藏
                addGameForm.style.display = 'none';
                gameForm.reset();

                // 显示成功消息
                showMessage(`Game "${game.name}" has been created successfully!`, 'success');

                // 重新加载游戏列表
                loadGames();

                // 如果有统计容器，也重新加载统计数据
                if (statsContainer) {
                    loadSystemStats();
                }
            })
            .catch(error => {
                console.error('Error creating game:', error);
                showMessage(`Failed to create game: ${error.message}`, 'error');
            })
            .finally(() => {
                // 恢复按钮状态
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            });
    }

    // 加载所有游戏
    function loadGames() {
        // 如果没有游戏容器，直接返回
        if (!gamesContainer) return;

        // 显示加载中
        gamesContainer.innerHTML = `
            <div class="w3-panel w3-light-grey w3-center">
                <p><i class="w3-spin fas fa-spinner"></i> Loading games...</p>
            </div>
        `;

        fetch('/api/admin/games')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server returned ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(games => {
                gamesContainer.innerHTML = '';

                if (games.length === 0) {
                    gamesContainer.innerHTML = `
                        <div class="w3-panel w3-pale-yellow">
                            <p><i class="fas fa-info-circle"></i> No games found. Add your first game!</p>
                        </div>
                    `;
                    return;
                }

                // 创建游戏卡片行布局
                const row = document.createElement('div');
                row.className = 'w3-row-padding';
                gamesContainer.appendChild(row);

                games.forEach(game => {
                    const gameCard = createGameCard(game);
                    row.appendChild(gameCard);

                    // 加载这个游戏的排行榜
                    loadGameLeaderboard(game.id);
                });
            })
            .catch(error => {
                console.error('Error loading games:', error);
                gamesContainer.innerHTML = `
                    <div class="w3-panel w3-pale-red">
                        <p><i class="fas fa-exclamation-triangle"></i> Failed to load games: ${error.message}</p>
                    </div>
                `;
            });
    }

    // 创建游戏卡片
    function createGameCard(game) {
        const col = document.createElement('div');
        col.className = 'w3-col l6 m12 s12';
        col.style.padding = '10px';

        const card = document.createElement('div');
        card.className = 'w3-card-4 game-card';
        card.id = `game-${game.id}`;

        // 卡片标题
        const header = document.createElement('header');
        header.className = 'w3-container w3-teal';
        header.innerHTML = `<h3>${game.name}</h3>`;

        // 卡片内容
        const content = document.createElement('div');
        content.className = 'w3-container';
        content.innerHTML = `
            <div class="w3-row w3-section">
                <div class="w3-col m6">
                    <p><b>Game ID:</b> ${game.id}</p>
                </div>
                <div class="w3-col m6">
                    <p><b>Max Score:</b> ${game.maxScore}</p>
                </div>
            </div>
            
            <div class="w3-panel w3-pale-blue">
                <h4><i class="fas fa-trophy w3-margin-right"></i>Leaderboard (Top 5)</h4>
                <div class="score-table">
                    <table class="w3-table w3-striped w3-bordered" id="leaderboard-${game.id}">
                        <thead>
                            <tr class="w3-light-grey">
                                <th>Rank</th>
                                <th>Player</th>
                                <th>Score</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td colspan="4" class="w3-center">Loading...</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        `;

        // 操作按钮
        const actions = document.createElement('div');
        actions.className = 'w3-container action-buttons w3-padding';
        actions.innerHTML = `
            <button class="w3-button w3-teal" onclick="viewAllScores(${game.id}, '${game.name}')">
                <i class="fas fa-list-ol w3-margin-right"></i>View All Scores
            </button>
            <button class="w3-button w3-red" onclick="confirmResetScores(${game.id}, '${game.name}')">
                <i class="fas fa-trash w3-margin-right"></i>Reset Scores
            </button>
        `;

        card.appendChild(header);
        card.appendChild(content);
        card.appendChild(actions);
        col.appendChild(card);

        return col;
    }

    // 加载游戏排行榜
    function loadGameLeaderboard(gameId) {
        fetch(`/api/admin/games/${gameId}/scores?limit=5`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server returned ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(scores => {
                const tbody = document.querySelector(`#leaderboard-${gameId} tbody`);
                if (!tbody) return;

                tbody.innerHTML = '';

                if (scores.length === 0) {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="4" class="w3-center">No scores yet</td>
                        </tr>
                    `;
                    return;
                }

                scores.forEach((score, index) => {
                    const row = document.createElement('tr');
                    const playerName = score.first_name && score.last_name ?
                        `${score.first_name} ${score.last_name}` :
                        (score.email || 'Anonymous');

                    const scoreDate = new Date(score.timestamp).toLocaleDateString();

                    row.innerHTML = `
                        <td>${index + 1}</td>
                        <td>${playerName}</td>
                        <td>${score.score_value}</td>
                        <td>${scoreDate}</td>
                    `;
                    tbody.appendChild(row);
                });
            })
            .catch(error => {
                console.error(`Error loading leaderboard for game ${gameId}:`, error);
                const tbody = document.querySelector(`#leaderboard-${gameId} tbody`);
                if (tbody) {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="4" class="w3-center w3-text-red">
                                <i class="fas fa-exclamation-circle"></i> Failed to load leaderboard
                            </td>
                        </tr>
                    `;
                }
            });
    }

    // 显示消息
    function showMessage(message, type = 'info') {
        // 检查是否已有消息容器
        let messageContainer = document.getElementById('message-container');

        if (!messageContainer) {
            // 创建消息容器
            messageContainer = document.createElement('div');
            messageContainer.id = 'message-container';
            messageContainer.className = 'w3-panel w3-display-container w3-animate-opacity';
            messageContainer.style.position = 'fixed';
            messageContainer.style.top = '20px';
            messageContainer.style.right = '20px';
            messageContainer.style.zIndex = '1000';
            messageContainer.style.maxWidth = '300px';
            document.body.appendChild(messageContainer);
        }

        // 设置消息类型样式
        let bgColor, textColor;
        switch (type) {
            case 'success':
                bgColor = 'w3-green';
                textColor = 'w3-text-white';
                break;
            case 'error':
                bgColor = 'w3-red';
                textColor = 'w3-text-white';
                break;
            case 'warning':
                bgColor = 'w3-yellow';
                textColor = 'w3-text-black';
                break;
            default:
                bgColor = 'w3-light-blue';
                textColor = 'w3-text-black';
        }

        // 创建消息元素
        const messageElement = document.createElement('div');
        messageElement.className = `w3-panel ${bgColor} ${textColor} w3-round-large w3-animate-right`;
        messageElement.innerHTML = `
            <span onclick="this.parentElement.remove()" 
                  class="w3-button w3-large w3-display-topright">&times;</span>
            <p>${message}</p>
        `;

        // 添加到容器
        messageContainer.appendChild(messageElement);

        // 设置自动消失
        setTimeout(() => {
            messageElement.classList.add('w3-animate-opacity');
            setTimeout(() => {
                messageElement.remove();
            }, 500);
        }, 5000);
    }

    // 全局函数 - 查看所有分数
    window.viewAllScores = function(gameId, gameName) {
        window.location.href = `/admin/games-scores.html?gameId=${gameId}&gameName=${encodeURIComponent(gameName)}`;
    };

    // 全局函数 - 确认重置分数
    window.confirmResetScores = function(gameId, gameName) {
        confirmMessage.innerHTML = `
            <p><i class="fas fa-exclamation-triangle w3-text-red w3-margin-right"></i>
            Are you sure you want to reset ALL scores for game <b>"${gameName}"</b>?</p>
            <p class="w3-text-red">This action cannot be undone!</p>
        `;

        confirmYesBtn.onclick = function() {
            resetGameScores(gameId, gameName);
            confirmModal.style.display = 'none';
        };

        confirmModal.style.display = 'block';
    };

    // 重置游戏分数
    function resetGameScores(gameId, gameName) {
        fetch(`/api/admin/games/${gameId}/scores`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server returned ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                showMessage(`All scores for "${gameName}" have been reset`, 'success');
                loadGameLeaderboard(gameId);

                // 如果有统计容器，也重新加载统计数据
                if (statsContainer) {
                    loadSystemStats();
                }
            })
            .catch(error => {
                console.error('Error resetting scores:', error);
                showMessage(`Failed to reset scores: ${error.message}`, 'error');
            });
    }

    // 页面加载时检查URL参数
    function checkUrlParameters() {
        const urlParams = new URLSearchParams(window.location.search);
        const gameId = urlParams.get('gameId');
        const gameName = urlParams.get('gameName');

        // 如果在games-scores.html页面并有gameId参数，加载该游戏的所有分数
        if (window.location.pathname.includes('games-scores.html') && gameId) {
            document.getElementById('game-title').textContent = gameName || `Game #${gameId}`;
            loadAllGameScores(gameId);
        }
    }

    // 加载游戏所有分数（用于games-scores.html页面）
    function loadAllGameScores(gameId) {
        const scoresTable = document.getElementById('scores-table');
        if (!scoresTable) return;

        scoresTable.innerHTML = `
            <tr>
                <td colspan="5" class="w3-center">
                    <i class="w3-spin fas fa-spinner"></i> Loading all scores...
                </td>
            </tr>
        `;

        fetch(`/api/admin/games/${gameId}/scores`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server returned ${response.status}: ${response.statusText}`);
                }
                return response.json();
            })
            .then(scores => {
                scoresTable.innerHTML = '';

                if (scores.length === 0) {
                    scoresTable.innerHTML = `
                        <tr>
                            <td colspan="5" class="w3-center">No scores found for this game</td>
                        </tr>
                    `;
                    return;
                }

                scores.forEach((score, index) => {
                    const row = document.createElement('tr');
                    const playerName = score.first_name && score.last_name ?
                        `${score.first_name} ${score.last_name}` :
                        (score.email || 'Anonymous');

                    const scoreDate = new Date(score.timestamp).toLocaleString();

                    row.innerHTML = `
                        <td>${index + 1}</td>
                        <td>${playerName}</td>
                        <td>${score.score_value}</td>
                        <td>${score.play_time} sec</td>
                        <td>${scoreDate}</td>
                    `;
                    scoresTable.appendChild(row);
                });

                // 添加分数统计
                const totalScores = scores.length;
                const avgScore = scores.reduce((sum, s) => sum + s.score_value, 0) / totalScores;
                const maxScore = Math.max(...scores.map(s => s.score_value));

                document.getElementById('score-stats').innerHTML = `
                    <div class="w3-panel w3-pale-blue w3-leftbar w3-border-blue">
                        <h4>Score Statistics</h4>
                        <div class="w3-row">
                            <div class="w3-col m4">
                                <p><b>Total Scores:</b> ${totalScores}</p>
                            </div>
                            <div class="w3-col m4">
                                <p><b>Average Score:</b> ${avgScore.toFixed(2)}</p>
                            </div>
                            <div class="w3-col m4">
                                <p><b>Highest Score:</b> ${maxScore}</p>
                            </div>
                        </div>
                    </div>
                `;
            })
            .catch(error => {
                console.error(`Error loading all scores for game ${gameId}:`, error);
                scoresTable.innerHTML = `
                    <tr>
                        <td colspan="5" class="w3-center w3-text-red">
                            <i class="fas fa-exclamation-circle"></i> Failed to load scores: ${error.message}
                        </td>
                    </tr>
                `;
            });
    }

    // 检查URL参数
    checkUrlParameters();
});

// 返回按钮
function goBack() {
    window.location.href = '/admin/games.html';
}