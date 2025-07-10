package org.OwlsGame.backend.games.tof;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class TofGameServiceImpl implements TofGameService {

    @Autowired
    private TofQuestionRepository questionRepo;

    @Autowired
    private TofScoreRepository scoreRepo;

    @Override
    public List<TofQuestion> getRandomQuestions(int num) {
        List<TofQuestion> all = questionRepo.findAll();
        Collections.shuffle(all, new Random());
        return all.stream().limit(num).collect(Collectors.toList());
    }

    @Override
    public boolean checkAnswer(Long questionId, boolean answer) {
        return questionRepo.findById(questionId)
                .map(q -> q.getAnswer().equals(answer))
                .orElse(false);
    }

    @Override
    public void saveScore(String username, int score) {
        TofScore s = new TofScore();
        s.setUsername(username);
        s.setScore(score);
        scoreRepo.save(s);
    }
}