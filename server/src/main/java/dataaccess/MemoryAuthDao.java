package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDao implements AuthDAO{
    private final Map<String, AuthData> AUTHUSERS = new HashMap<>();

    public void createAuthUser(AuthData user) throws DataAccessException {
        AUTHUSERS.put(user.authToken(), user);
    }

    public AuthData getByUsername(String username) throws DataAccessException {
        for (AuthData user : AUTHUSERS.values()) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("user not found");
    }

    public AuthData getByToken(String authToken) throws DataAccessException {
        AuthData user = AUTHUSERS.get(authToken);
        if (user == null) {
            throw new DataAccessException("Invalid authToken");
        }
        return user;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        AUTHUSERS.remove(authToken);
    }

    public void clear() {
        AUTHUSERS.clear();
    }
}