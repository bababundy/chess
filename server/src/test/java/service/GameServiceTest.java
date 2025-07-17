package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.BeforeAll;
import requests.*;
import results.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private static UserDao userdao;
    private static AuthDao authdao;
    private static GameDao gamedao;

    @BeforeAll
    static void initiate() {
        userdao = new UserDao();
        authdao = new AuthDao();
        gamedao = new GameDao();
    }

    @BeforeEach
    void setup() throws DataAccessException {
        userdao.clear();
        authdao.clear();
        gamedao.clear();
        UserData user = new UserData("kolt", "password", "kolt@example.com");
        userdao.createUser(user);
        AuthData authUser = new AuthData("abcd1234", "kolt");
        authdao.createAuthUser(authUser);
        GameData existingGame = new GameData(1, null, "jimmy", "existing game", new ChessGame());
        gamedao.createGame(existingGame);
    }

    @Test
    void badCreate() throws DataAccessException {
        CreateRequest request = new CreateRequest("wrongToken", "newgame");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            CreateResult result = GameService.create(request);
        });
        assertTrue(ex.getMessage().contains("Invalid"));
    }

    @Test
    void validCreate() throws DataAccessException {
        CreateRequest request = new CreateRequest("abcd1234", "newgame");
        CreateResult result = GameService.create(request);
        assertTrue(result.gameID()>0);
        assertSame("newgame", (gamedao.getGameByName("newgame")).gameName());
    }

    @Test
    void badTokenJoin() {
        JoinRequest request = new JoinRequest("wrongToken", "WHITE", 1);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            JoinResult result = GameService.join(request);
        });
        assertTrue(ex.getMessage().contains("AuthToken"));
    }

    @Test
    void badGameIDJoin() {
        JoinRequest request = new JoinRequest("abcd1234", "WHITE", 2);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            JoinResult result = GameService.join(request);
        });
        assertTrue(ex.getMessage().contains("Bad"));
    }

    @Test
    void alreadyTakenJoin() {
        JoinRequest request = new JoinRequest("abcd1234", "BLACK", 1);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            JoinResult result = GameService.join(request);
        });
        assertTrue(ex.getMessage().contains("Taken"));
    }

    @Test
    void validJoin() throws DataAccessException {
        JoinRequest request = new JoinRequest("abcd1234", "WHITE", 1);
        JoinResult result = GameService.join(request);
        assertEquals("kolt", (gamedao.getGameByID(1)).whiteUsername());
        assertNull(result.message());
    }

    @Test
    void badList() {
        ListRequest request = new ListRequest("badToken");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            ListResult result = GameService.list(request);});
        assertTrue(ex.getMessage().contains("Invalid"));
    }

    @Test
    void validList() throws DataAccessException {
        GameData existingGame = new GameData(2, "", "ted", "second game", new ChessGame());
        gamedao.createGame(existingGame);
        ListRequest request = new ListRequest("abcd1234");
        ListResult result = GameService.list(request);
        assertEquals(gamedao.getList(), result.games());
        assertNull(result.message());
    }

}