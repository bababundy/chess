package dataaccess;

import model.*;

import java.util.*;

public class MemoryDatabase {
    public final Map<String, UserData> users = new HashMap<>(); //String = username
    public final Map<String, AuthData> authUsers = new HashMap<>(); // key = authToken
    public final Map<Integer, GameData> games = new HashMap<>();// Integer = gameID

    private static final MemoryDatabase INSTANCE = new MemoryDatabase();

    public static MemoryDatabase getInstance() {
        return INSTANCE;
    }
}
