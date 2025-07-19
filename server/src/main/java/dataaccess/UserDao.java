package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData newUser);
    UserData getUser(String username) throws DataAccessException;
    void clear();
}
