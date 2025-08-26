DELETE FROM tof_questions;
DELETE FROM scores;
DELETE FROM games;

-- Insert games first
INSERT INTO games (id, name, max_score) VALUES (1, 'True/False', 1000);
INSERT INTO games (id, name, max_score) VALUES (2, 'Quiz Game', 500);

-- Insert sample scores
INSERT INTO scores (user_id, game_id, score_value, timestamp, email, play_time) 
VALUES (1, 1, 2, CURRENT_TIMESTAMP, 'jreeylee92@outlook.com', 7);

INSERT INTO scores (user_id, game_id, score_value, timestamp, email, play_time) 
VALUES (1, 1, 5, CURRENT_TIMESTAMP, 'jreeylee92@outlook.com', 12);

INSERT INTO scores (user_id, game_id, score_value, timestamp, email, play_time) 
VALUES (1, 2, 3, CURRENT_TIMESTAMP, 'test@example.com', 15);

INSERT INTO tof_questions (id, content, answer) VALUES (1, 'Humans share 50% of their DNA with bananas.', TRUE);
INSERT INTO tof_questions (id, content, answer) VALUES (2, 'The Earth is the only planet in our solar system that has a moon.', FALSE);
INSERT INTO tof_questions (id, content, answer) VALUES (3, 'Goldfish have a memory span of only three seconds.', FALSE);
INSERT INTO tof_questions (id, content, answer) VALUES (4, 'The Eiffel Tower can be 15 cm taller during the summer.', TRUE);
INSERT INTO tof_questions (id, content, answer) VALUES (5, 'Honey never spoils.', TRUE);