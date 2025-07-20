package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.daoInterfaces.AuthDAO;
import dataaccess.daoInterfaces.GameDAO;
import dataaccess.daoInterfaces.UserDAO;
import dataaccess.memory.MemoryAuthDao;
import dataaccess.memory.MemoryGameDao;
import dataaccess.memory.MemoryUserDao;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.*;
import results.*;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private GameService gameService;
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        // Set up fresh DAOs
        authDAO = new MemoryAuthDao();
        userDAO = new MemoryUserDao();
        gameDAO = new MemoryGameDao();

        // Inject into DAOFacade
        DAOFacade.authDAO = authDAO;
        DAOFacade.userDAO = userDAO;
        DAOFacade.gameDAO = gameDAO;

        // Create GameService instance using DAOs
        gameService = new GameService(gameDAO, authDAO);

        // Set up test data
        userDAO.createUser(new UserData("kolt", "password", "kolt@example.com"));
        authDAO.createAuthUser(new AuthData("abcd1234", "kolt"));
        gameDAO.createGame(new GameData(1, null, "jimmy", "existing game", new ChessGame()));
    }

    @Test
    void badCreate() {
        CreateRequest request = new CreateRequest("wrongToken", "newgame");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.create(request);
        });
        assertTrue(ex.getMessage().contains("Invalid"));
    }

    @Test
    void validCreate() throws DataAccessException {
        CreateRequest request = new CreateRequest("abcd1234", "newgame");
        CreateResult result = gameService.create(request);
        assertTrue(result.gameID() > 0);
        assertEquals("newgame", gameDAO.getGameByID(result.gameID()).gameName());
    }

    @Test
    void badTokenJoin() {
        JoinRequest request = new JoinRequest("wrongToken", "WHITE", 1);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.join(request);
        });
        assertTrue(ex.getMessage().contains("AuthToken"));
    }

    @Test
    void badGameIDJoin() {
        JoinRequest request = new JoinRequest("abcd1234", "WHITE", 2); // game 2 doesn't exist
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.join(request);
        });
        assertTrue(ex.getMessage().contains("Bad"));
    }

    @Test
    void alreadyTakenJoin() {
        JoinRequest request = new JoinRequest("abcd1234", "BLACK", 1); // black already taken
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.join(request);
        });
        assertTrue(ex.getMessage().contains("Taken"));
    }

    @Test
    void validJoin() throws DataAccessException {
        JoinRequest request = new JoinRequest("abcd1234", "WHITE", 1);
        JoinResult result = gameService.join(request);
        assertEquals("kolt", gameDAO.getGameByID(1).whiteUsername());
        assertNull(result.message());
    }

    @Test
    void badList() {
        ListRequest request = new ListRequest("badToken");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.list(request);
        });
        assertTrue(ex.getMessage().contains("Invalid"));
    }

    @Test
    void validList() throws DataAccessException {
        gameDAO.createGame(new GameData(2, "", "ted", "second game", new ChessGame()));
        ListRequest request = new ListRequest("abcd1234");
        ListResult result = gameService.list(request);
        assertEquals(2, result.games().size());
        assertNull(result.message());
    }
}
