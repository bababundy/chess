package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.daointerfaces.UserDAO;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDao implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    public void createUser(UserData newUser) {
        users.put(newUser.username(), newUser);
    }

    public UserData getUser(String username) throws DataAccessException {
        UserData user = users.get(username);
        if (user == null) {
            throw new DataAccessException("user not found");
        }
        return user;
    }

    public void clear() {
        users.clear();
    }
}
