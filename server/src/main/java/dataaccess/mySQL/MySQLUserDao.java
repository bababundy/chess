package dataaccess.mySQL;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class MySQLUserDao implements UserDAO {
    @Override
    public void createUser(UserData newUser) {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }
}
