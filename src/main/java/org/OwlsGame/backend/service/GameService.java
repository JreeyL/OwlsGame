package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.Game;
import java.util.List;

public interface GameService {
    Game getGameById(int id);
    List<Game> getAllGames();
}
