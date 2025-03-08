package org.OwlsGame.backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "maxScore", nullable = false)
    private int maxScore;

    // Constructors
    public Game() {}

    public Game(String name, int maxScore) {
        this.name = name;
        this.maxScore = maxScore;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    // Methods
    public void startGame() {
        // Logic to start the game
    }

    public int calculateScore() {
        // Logic to calculate the score
        return 0;
    }

    public void resetGame() {
        this.maxScore = 0;
        // Logic to reset the game
    }
}