package dataaccess.mySQL;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MySQLGameDaoTest {
    private MySQLGameDao dao;


    @BeforeEach
    public void setup() throws DataAccessException {
        dao = new MySQLGameDao();
        dao.clear();
    }

    @Test
    public void testCreateGameFailsWithNullGameID() {
        ChessGame game = new ChessGame();
        GameData invalidGame = new GameData(null, "whiteUser", "blackUser", "Test Game", game);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dao.createGame(invalidGame);
        });

        String expectedMessage = "request";
        assertTrue(exception.getMessage().toLowerCase().contains(expectedMessage), "Exception message should contain: " + expectedMessage);
    }

    @Test
    void createGameAndGetByID() {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "testGame", game);

        try {
            dao.createGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        GameData retrieved = null;
        try {
            retrieved = dao.getGameByID(1);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertEquals(gameData.gameName(), retrieved.gameName());
        assertEquals(gameData.whiteUsername(), retrieved.whiteUsername());
        assertEquals(gameData.blackUsername(), retrieved.blackUsername());
    }

    @Test
    public void testUpdateGameFailsWithNullName() {
        ChessGame game = new ChessGame();
        GameData brokenGame = new GameData(1, "white", "black", null, game); // null game name violates schema

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.updateGame(1, brokenGame);
        });

        assertTrue(ex.getMessage().contains("DataAccessException") || ex.getMessage().contains("request"), "Expected failure from update");
    }

    @Test
    void updateGame() {
        ChessGame game = new ChessGame();
        GameData original = new GameData(2, "white", "black", "toUpdate", game);
        try {
            dao.createGame(original);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        GameData updated = new GameData(2, "newWhite", "newBlack", "updatedName", game);
        try {
            dao.updateGame(2, updated);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        GameData result = null;
        try {
            result = dao.getGameByID(2);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertEquals("newWhite", result.whiteUsername());
        assertEquals("newBlack", result.blackUsername());
        assertEquals("updatedName", result.gameName());
    }

    @Test
    public void testGetGameByNameFailsNotFound() throws DataAccessException {
        dao.clear();

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.getGameByName("NonExistentGame");
        });

        assertTrue(ex.getMessage().contains("game not found"), "Expected 'game not found' message");
    }

    @Test
    void getGameByName() {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(3, "white", "black", "findMe", game);
        try {
            dao.createGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        GameData retrieved = null;
        try {
            retrieved = dao.getGameByName("findMe");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertEquals(3, retrieved.gameID());
    }

    @Test
    public void testGetGameByIDFailsNotFound() throws DataAccessException {
        dao.clear();

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.getGameByID(99999); // Arbitrary ID that doesn't exist
        });

        assertTrue(ex.getMessage().contains("game not found"), "Expected 'game not found' message");
    }

    @Test
    void getGameByID() {
        ChessGame game = new ChessGame();
        GameData expected = new GameData(101, "whiteUser", "blackUser", "gameByID", game);
        try {
            dao.createGame(expected);
            GameData result = dao.getGameByID(101);

            assertNotNull(result, "Game should not be null");
            assertEquals(expected.gameID(), result.gameID());
            assertEquals(expected.whiteUsername(), result.whiteUsername());
            assertEquals(expected.blackUsername(), result.blackUsername());
            assertEquals(expected.gameName(), result.gameName());
        } catch (DataAccessException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void getNumGames() {
        try {
            dao.clear();
            ChessGame game = new ChessGame();
            dao.createGame(new GameData(201, "a", "b", "firstGame", game));
            dao.createGame(new GameData(202, "c", "d", "secondGame", game));

            Integer count = dao.getNumGames();
            assertEquals(2, count);
        } catch (DataAccessException e) {
            fail("Unexpected exception during getNumGames test: " + e.getMessage());
        }
    }

    @Test
    void testGetNumGamesFailsGracefully() {
        MySQLGameDao brokenDao = new MySQLGameDao() {
            @Override
            public Integer getNumGames() {
                throw new RuntimeException("Simulated DB failure");
            }
        };

        try {
            Integer count = brokenDao.getNumGames();
            fail("Expected RuntimeException due to simulated DB failure, but got count: " + count);
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Simulated DB failure"));
        }
    }

    @Test
    public void testGetListEmptyWhenNoGamesExist() throws DataAccessException {
        dao.clear();

        List<GameData> games = dao.getList();

        assertNotNull(games, "List should not be null");
        assertEquals(0, games.size(), "Expected empty list of games");
    }

    @Test
    void getList() throws DataAccessException {
        try {
            dao.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        ChessGame game = new ChessGame();
        try {
            dao.createGame(new GameData(10, "a", "b", "game1", game));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            dao.createGame(new GameData(11, "c", "d", "game2", game));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        ArrayList<GameData> list = dao.getList();
        assertEquals(2, list.size());
    }

    @Test
    void clear() throws DataAccessException {
        ChessGame game = new ChessGame();
        try {
            dao.createGame(new GameData(20, "w", "b", "clearMe", game));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            dao.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        ArrayList<GameData> list = dao.getList();
        assertTrue(list.isEmpty());
    }

}