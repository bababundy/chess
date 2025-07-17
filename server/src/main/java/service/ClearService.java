package service;

import dataaccess.*;
import results.ClearResult;

public class ClearService {
    private static final UserDao userDAO = new UserDao();
    private static final AuthDao authDAO = new AuthDao();
    private static final GameDao gameDAO = new GameDao();

    public static ClearResult clear() {
        //1. database operations (clear users, clear games, clear authtokens)
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        //3. create result and return
        return new ClearResult(null);
    }
}

