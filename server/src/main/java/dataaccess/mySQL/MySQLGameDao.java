package dataaccess.mySQL;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;

public class MySQLGameDao implements GameDAO {
    @Override
    public void createGame(GameData newGame) throws DataAccessException {

    }

    @Override
    public void updateGame(Integer gameID, GameData newGame) throws DataAccessException {

    }

    @Override
    public GameData getGameByName(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGameByID(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Integer getNumGames() {
        return 0;
    }

    @Override
    public ArrayList<GameData> getList() {
        return null;
    }

    @Override
    public void clear() {

    }
}
