package org.OwlsGame.backend.games.tof;

import java.util.List;

public interface TofGameService {
    List<TofQuestion> getRandomQuestions(int num);
    boolean checkAnswer(Long questionId, boolean answer);
    void saveScore(String username, int score);
}