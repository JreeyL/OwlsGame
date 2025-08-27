-- 清理TOF问题数据
DELETE FROM tof_questions;

-- 插入TOF问题
INSERT INTO tof_questions (id, content, answer) VALUES (1, 'Humans share 50% of their DNA with bananas.', TRUE);
INSERT INTO tof_questions (id, content, answer) VALUES (2, 'The Earth is the only planet in our solar system that has a moon.', FALSE);
INSERT INTO tof_questions (id, content, answer) VALUES (3, 'Goldfish have a memory span of only three seconds.', FALSE);
INSERT INTO tof_questions (id, content, answer) VALUES (4, 'The Eiffel Tower can be 15 cm taller during the summer.', TRUE);
INSERT INTO tof_questions (id, content, answer) VALUES (5, 'Honey never spoils.', TRUE);

-- 添加默认游戏数据 (如果不存在)
INSERT INTO games (id, name, max_score)
SELECT 1, 'True/False', 1000
    WHERE NOT EXISTS (SELECT 1 FROM games WHERE id = 1);

-- 添加TOF游戏专用数据 (如果不存在)
INSERT INTO tof_games (id, round_count, allow_skip)
SELECT 1, 10, false
    WHERE NOT EXISTS (SELECT 1 FROM tof_games WHERE id = 1);