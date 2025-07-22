package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.daointerfaces.GameDAO;
import model.GameData;

import java.util.*;

public class MySqlGameDao extends SqlDaoHelper implements GameDAO {
    @Override
    public void createGame(GameData newGame) throws DataAccessException {
        if(newGame.gameName() == null || newGame.game() == null || newGame.gameID() == null){
            throw new DataAccessException("bad request");
        }
        String statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, gameJson) VALUES (?, ?, ?, ?, ?)";
        String gameJson = new Gson().toJson(newGame.game());
        try {
            executeUpdate(statement, newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), gameJson);
        } catch (DataAccessException e) {
            if(e.getMessage().contains("500")){
                throw new DataAccessException(e.getMessage());
            } else {
                throw new RuntimeException("Failed to create game", e);
            }
        }
    }

    @Override
    public void updateGame(Integer gameID, GameData newGame) throws DataAccessException {
        if(newGame.gameName() == null || gameID == null || gameID < 1){
            throw new DataAccessException("bad request");
        }
        String statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJson = ? WHERE gameID = ?";
        String gameJson = new Gson().toJson(newGame.game());
        executeUpdate(statement, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), gameJson, gameID);
    }

    @Override
    public GameData getGameByName(String gameName) throws DataAccessException {
        var statement = "SELECT * FROM games WHERE gameName = ?";
        List<HashMap<String, Object>> rows = executeQuery(statement, gameName);
        if (rows.isEmpty()) {
            throw new DataAccessException("game not found");
        }
        return parseGame(rows.getFirst());
    }

    @Override
    public GameData getGameByID(Integer gameID) throws DataAccessException {
        var statement = "SELECT * FROM games WHERE gameID = ?";
        List<HashMap<String, Object>> rows = executeQuery(statement, gameID);
        if (rows.isEmpty()) {
            throw new DataAccessException("game not found");
        }
        return parseGame(rows.getFirst());
    }

    @Override
    public Integer getNumGames() {
        var statement = "SELECT COUNT(*) as count FROM games";
        try {
            List<HashMap<String, Object>> result = executeQuery(statement);
            Number countRaw = (Number) result.getFirst().get("count");
            return countRaw.intValue();
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public ArrayList<GameData> getList() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try {
            var rows = executeQuery("SELECT * FROM games");
            for (var row : rows) {
                result.add(parseGame(row));
            }
        } catch (DataAccessException e) {
            if(e.getMessage().contains("500")){
                throw new DataAccessException(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    private GameData parseGame(HashMap<String, Object> row) {
        Number gameIDRaw = (Number) row.get("gameID");
        Integer gameID = gameIDRaw.intValue();
        String whiteUsername = (String) row.get("whiteUsername");
        String blackUsername = (String) row.get("blackUsername");
        String gameName = (String) row.get("gameName");
        String gameJson = (String) row.get("gameJson");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }
}
