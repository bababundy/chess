package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class GameDao {
    private final Map<Integer, GameData> games = MemoryDatabase.getInstance().games;

    public void createGame(GameData newGame) throws DataAccessException {
        if (games.containsKey(newGame.gameID())) {
            throw new DataAccessException("already taken");
        }
        games.put(newGame.gameID(), newGame);
    }

    public void updateGame(Integer gameID, GameData newGame) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("game not found");
        }
        games.put(gameID, newGame);
    }

    public GameData getGameByName(String gameName) throws DataAccessException {
        for (GameData game : games.values()) {
            if (Objects.equals(game.gameName(), gameName)) {
                return game;
            }
        }
        throw new DataAccessException("game not found");
    }

    public GameData getGameByID(Integer gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("game not found");
        }
        return game;
    }

    public Integer getNumGames () {
        return games.size();
    }

    public ArrayList<GameData> getList() {
        return new ArrayList<>(games.values());
    }

    public void clear() {
        games.clear();
    }
}
