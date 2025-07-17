package service;

import chess.ChessGame;
import dataaccess.MemoryDatabase;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
class ClearServiceTest {
    private static final Map<String, AuthData> AUTHUSERS = MemoryDatabase.getInstance().authUsers;
    private static final Map<Integer, GameData> GAMES = MemoryDatabase.getInstance().games;
    private static final Map<String, UserData> USERS = MemoryDatabase.getInstance().users;

    @Test
    void clear() {
        AUTHUSERS.put("abcd1234", new AuthData("abcd1234", "kolt"));
        GAMES.put(1, new GameData(1, "white", "black", "cool game", new ChessGame()));
        USERS.put("kolt", new UserData("kolt", "password", "kolt@example.com"));ClearService.clear();
        ClearService.clear();
        assertTrue(AUTHUSERS.isEmpty(), "Auth tokens were not cleared");
        assertTrue(USERS.isEmpty(), "Users were not cleared");
        assertTrue(GAMES.isEmpty(), "Games were not cleared");
    }
}