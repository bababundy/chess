package service;

import dataaccess.*;
import results.ClearResult;

public class ClearService {
    private static final UserDao USERDAO = new UserDao();
    private static final AuthDao AUTHDAO = new AuthDao();
    private static final GameDao GAMEDAO = new GameDao();

    public static ClearResult clear() {
        //1. database operations (clear users, clear games, clear authtokens)
        AUTHDAO.clear();
        GAMEDAO.clear();
        USERDAO.clear();
        //3. create result and return
        return new ClearResult(null);
    }
}

