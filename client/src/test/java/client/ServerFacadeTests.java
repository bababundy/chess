package client;

import org.junit.jupiter.api.*;
import requests.*;
import results.*;
import server.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        facade.clear();
    }

    @AfterEach
    public void reset() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void validRegister() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void duplicateRegister() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        ResponseException ex = assertThrows(ResponseException.class, () -> {
            facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        });
        assertTrue(ex.getMessage().contains("already taken"), "Should fail on duplicate token");
    }

    @Test
    public void goodLogin() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        LoginResult result = facade.login(new LoginRequest("player1", "password"));
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void incorrectPasswordLogin() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        ResponseException ex = assertThrows(ResponseException.class, () -> {
            facade.login(new LoginRequest("player1", "wrongpassword"));
        });
        assertTrue(ex.getMessage().contains("Incorrect"), "Should fail on wrong password");
    }

    @Test
    public void goodCreate() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        CreateResult result2 = facade.createGame(new CreateRequest(result.authToken(), "testgame"));
        assertNotNull(result2.gameID());
        assertNull(result2.message());
    }

    @Test
    public void nullGameNameCreate() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        ResponseException ex = assertThrows(ResponseException.class, () -> {
            facade.createGame(new CreateRequest(result.authToken(), null));
        });
        assertTrue(ex.getMessage().contains("request"), "Should fail on null gameName");
    }

    @Test
    public void goodJoin() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        CreateResult result2 = facade.createGame(new CreateRequest(result.authToken(), "testgame"));
        JoinResult result3 = facade.joinGame(new JoinRequest(result.authToken(), "WHITE", result2.gameID()));
        assertNull(result3.message());
    }

    @Test
    public void badGameIDJoin() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        CreateResult result2 = facade.createGame(new CreateRequest(result.authToken(), "testgame"));
        ResponseException ex = assertThrows(ResponseException.class, () -> {
            facade.joinGame(new JoinRequest(result.authToken(), "WHITE", 999));
        });
        assertTrue(ex.getMessage().contains("request"), "Should fail on bad gameName");
    }

    @Test
    public void goodList() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.createGame(new CreateRequest(result.authToken(), "testgame"));
        ListResult result3 = facade.listGames(new ListRequest(result.authToken()));
        assertEquals(1, result3.games().size());
    }

    @Test
    public void badAuthTokenList() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.createGame(new CreateRequest(result.authToken(), "testgame"));
        ResponseException ex = assertThrows(ResponseException.class, () -> {
            facade.listGames(new ListRequest("incorrectAuthToken"));
        });
        assertTrue(ex.getMessage().contains("request"), "Should fail on bad gameName");
    }
}