package org.OwlsGame.backend.models;



public class Game {
    private int id;
    private String name;
    private int maxScore;

    // Constructors
    public Game() {}

    public Game(int id, String name, int maxScore) {
        this.id = id;
        this.name = name;
        this.maxScore = maxScore;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        // Logic to reset the game
    }
}