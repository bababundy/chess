package service;

import dataaccess.*;
import dataaccess.daoInterfaces.*;
import results.ClearResult;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearResult clear() throws DataAccessException {
        //1. database operations (clear users, clear games, clear authtokens)
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        //3. create result and return
        return new ClearResult(null);
    }
}

