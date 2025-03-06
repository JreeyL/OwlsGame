package org.OwlsGame.backend.service;

import org.OwlsGame.backend.models.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameDAO gameDAO;

    @Override
    public Game getGameById(int id) {
        Game game = null;
        try {
            game = gameDAO.getGameById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return game;
    }

    @Override
    public List<Game> getAllGames() {
        List<Game> games = null;
        try {
            games = gameDAO.getAllGames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }
}