-- 创建问题表
CREATE TABLE IF NOT EXISTS tof_questions (
                                             id BIGINT PRIMARY KEY,
                                             content VARCHAR(255),
    answer BOOLEAN
    );

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    is_locked BOOLEAN DEFAULT FALSE,
    login_attempts INT DEFAULT 0,
    lock_until TIMESTAMP
    );

-- 创建游戏表
CREATE TABLE IF NOT EXISTS games (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    max_score INT NOT NULL
    );

-- 创建TOF游戏表（用于继承）
CREATE TABLE IF NOT EXISTS tof_games (
                                         id BIGINT PRIMARY KEY,
                                         round_count INT NOT NULL DEFAULT 10,
                                         allow_skip BOOLEAN NOT NULL DEFAULT false,
                                         FOREIGN KEY (id) REFERENCES games(id)
    );

-- 创建分数表
CREATE TABLE IF NOT EXISTS scores (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      user_id BIGINT NOT NULL,
                                      game_id BIGINT NOT NULL,
                                      score_value INT NOT NULL,
                                      timestamp TIMESTAMP NOT NULL,
                                      email VARCHAR(255) NOT NULL,
    play_time INT NOT NULL
    );

-- 创建会话表
CREATE TABLE IF NOT EXISTS sessions (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        session_id VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    creation_time TIMESTAMP NOT NULL,
    last_accessed_time TIMESTAMP NOT NULL,
    is_valid BOOLEAN NOT NULL DEFAULT TRUE,
    last_played_game_id BIGINT,
    favorite_game_id BIGINT,
    cumulative_score INT DEFAULT 0
    );

-- 创建OTP表
CREATE TABLE IF NOT EXISTS otps (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    email VARCHAR(255) NOT NULL,
    otp VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
    );

-- 创建TOF分数表 (兼容旧系统)
CREATE TABLE IF NOT EXISTS tof_scores (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          username VARCHAR(255),
    score INT
    );