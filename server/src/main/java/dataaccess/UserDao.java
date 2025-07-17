package dataaccess;

import model.UserData;

import java.util.Map;

public class UserDao{
    private static final Map<String, UserData> USERS = MemoryDatabase.getInstance().users;

    public void createUser(UserData newUser) {
        USERS.put(newUser.username(), newUser);
    }

    public static UserData getUser(String username) throws DataAccessException {
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
