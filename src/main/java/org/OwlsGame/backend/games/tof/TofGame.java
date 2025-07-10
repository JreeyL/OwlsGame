package org.OwlsGame.backend.games.tof;

import jakarta.persistence.*;
import org.OwlsGame.backend.models.Game;

@Entity
@Table(name = "tof_games")
public class TofGame extends Game {

    @Column(nullable = false)
    private int roundCount;

    @Column(nullable = false)
    private boolean allowSkip;

    // 你可以添加更多Tof专有字段

    public TofGame() {}

    public TofGame(String name, int maxScore, int roundCount, boolean allowSkip) {
        super(name, maxScore);
        this.roundCount = roundCount;
        this.allowSkip = allowSkip;
    }

    public int getRoundCount() { return roundCount; }
    public void setRoundCount(int roundCount) { this.roundCount = roundCount; }

    public boolean isAllowSkip() { return allowSkip; }
    public void setAllowSkip(boolean allowSkip) { this.allowSkip = allowSkip; }
}