package dataaccess.daoInterfaces;

import dataaccess.DataAccessException;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO { //compare to dataaccess in petshop
    void createGame(GameData newGame) throws DataAccessException;
    void updateGame(Integer gameID, GameData newGame) throws DataAccessException;
    GameData getGameByName(String gameName) throws DataAccessException;
    GameData getGameByID(Integer gameID) throws DataAccessException;
    Integer getNumGames ();
    ArrayList<GameData> getList() throws DataAccessException;
    void clear() throws DataAccessException;
}
