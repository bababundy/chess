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
    private static final Map<String, AuthData> authUsers = MemoryDatabase.getInstance().authUsers;
    private static final Map<Integer, GameData> games = MemoryDatabase.getInstance().games;
    private static final Map<String, UserData> users = MemoryDatabase.getInstance().users;

    @Test
    void clear() {
        authUsers.put("abcd1234", new AuthData("abcd1234", "kolt"));
        games.put(1, new GameData(1, "white", "black", "cool game", new ChessGame()));
        users.put("kolt", new UserData("kolt", "password", "kolt@example.com"));ClearService.clear();
        ClearService.clear();
        assertTrue(authUsers.isEmpty(), "Auth tokens were not cleared");
        assertTrue(users.isEmpty(), "Users were not cleared");
        assertTrue(games.isEmpty(), "Games were not cleared");
    }
}