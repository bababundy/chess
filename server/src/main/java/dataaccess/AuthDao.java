package dataaccess;

import model.AuthData;

import java.util.Map;

public class AuthDao {
    public static final Map<String, AuthData> AUTHUSERS = MemoryDatabase.getInstance().authUsers;

    public static void createAuthUser(AuthData user) throws DataAccessException {
        AUTHUSERS.put(user.authToken(), user);
    }

    public static AuthData getByUsername(String username) throws DataAccessException {
        for (AuthData user : AUTHUSERS.values()) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("user not found");
    }

    public static AuthData getByToken(String authToken) throws DataAccessException {
        AuthData user = AUTHUSERS.get(authToken);
        if (user == null) {
            throw new DataAccessException("Invalid authToken");
        }
        return user;
    }

    public void deleteAuthUser(String authToken) {
        AUTHUSERS.remove(authToken);
    }

    public void clear() {
        AUTHUSERS.clear();
    }
}