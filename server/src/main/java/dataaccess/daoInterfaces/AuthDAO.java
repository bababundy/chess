package dataaccess.daoInterfaces;

import dataaccess.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    void createAuthUser(AuthData auth) throws DataAccessException;
    AuthData getByToken(String authToken) throws DataAccessException;
    AuthData getByUsername(String username) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
