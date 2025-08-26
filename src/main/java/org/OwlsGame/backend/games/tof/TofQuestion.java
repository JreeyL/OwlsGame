package org.OwlsGame.backend.games.tof;

import jakarta.persistence.*;

@Entity
@Table(name = "tof_questions")
public class TofQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private Boolean answer;

    // 可添加难度等字段

    public TofQuestion() {}

    public TofQuestion(String content, Boolean answer) {
        this.content = content;
        this.answer = answer;
    }

    public Long getId() { return id; }

    // 添加 setId 方法
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Boolean getAnswer() { return answer; }
    public void setAnswer(Boolean answer) { this.answer = answer; }
}