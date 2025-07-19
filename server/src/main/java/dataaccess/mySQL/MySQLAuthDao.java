package dataaccess.mySQL;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class MySQLAuthDao implements AuthDAO {
    @Override
    public void createAuthUser(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getByToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
