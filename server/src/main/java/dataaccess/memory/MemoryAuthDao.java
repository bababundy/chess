package dataaccess.memory;

import dataaccess.daointerfaces.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDao implements AuthDAO {
    private final Map<String, AuthData> authUsers = new HashMap<>();

    public void createAuthUser(AuthData user) throws DataAccessException {
        authUsers.put(user.authToken(), user);
    }

    public AuthData getByUsername(String username) throws DataAccessException {
        for (AuthData user : authUsers.values()) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("user not found");
    }

    public AuthData getByToken(String authToken) throws DataAccessException {
        AuthData user = authUsers.get(authToken);
        if (user == null) {
            throw new DataAccessException("Invalid authToken");
        }
        return user;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        authUsers.remove(authToken);
    }

    public void clear() {
        authUsers.clear();
    }
}