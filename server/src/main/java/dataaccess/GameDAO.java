package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public interface GameDAO { //compare to dataaccess in petshop
    void createGame(GameData newGame) throws DataAccessException;
    void updateGame(Integer gameID, GameData newGame) throws DataAccessException;
    GameData getGameByName(String gameName) throws DataAccessException;
    GameData getGameByID(Integer gameID) throws DataAccessException;
    Integer getNumGames ();
    ArrayList<GameData> getList();
    void clear();
}
