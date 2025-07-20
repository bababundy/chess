package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.daoInterfaces.UserDAO;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDao implements UserDAO {
    private final Map<String, UserData> USERS = new HashMap<>();

    public void createUser(UserData newUser) {
        USERS.put(newUser.username(), newUser);
    }

    public UserData getUser(String username) throws DataAccessException {
        UserData user = USERS.get(username);
        if (user == null) {
            throw new DataAccessException("user not found");
        }
        return user;
    }

    public void clear() {
        USERS.clear();
    }
}
