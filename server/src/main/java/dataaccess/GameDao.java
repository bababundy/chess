package dataaccess;

import model.GameData;
import org.eclipse.jetty.server.Authentication;

import java.util.ArrayList;

public class GameDao {
    void createGame() throws DataAccessException {}

    GameData getGame(String username) throws DataAccessException {
        return null;
    }

    void updateGame(Authentication.User u) throws DataAccessException {}

    ArrayList<GameData> listGames(String username) throws DataAccessException {
        return null;
    }

}
