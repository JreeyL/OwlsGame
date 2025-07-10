package org.OwlsGame.backend.games.tof;

import jakarta.persistence.*;

@Entity
@Table(name = "tof_scores")
public class TofScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Integer score;

    public TofScore() {}

    public TofScore(String username, Integer score) {
        this.username = username;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}