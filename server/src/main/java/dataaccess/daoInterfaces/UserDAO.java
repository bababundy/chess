package dataaccess.daoInterfaces;

import dataaccess.DataAccessException;
import model.UserData;

public interface UserDAO {
    void createUser(UserData newUser);
    UserData getUser(String username) throws DataAccessException;
    void clear();
}
