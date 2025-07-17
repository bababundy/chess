package dataaccess;

import model.UserData;

import java.util.Map;

public class UserDao{
    private static final Map<String, UserData> users = MemoryDatabase.getInstance().users;

    public void createUser(UserData newUser) {
        users.put(newUser.username(), newUser);
    }

    public static UserData getUser(String username) throws DataAccessException {
        UserData user = users.get(username);
        if (user == null) throw new DataAccessException("user not found");
        return user;
    }

    public void clear() {
        users.clear();
    }
}
