package dataaccess;

import org.eclipse.jetty.server.Authentication;

public class UserDao{

    void createUser(Authentication.User u) throws DataAccessException {}

    Authentication.User getUser(String username) throws DataAccessException {
        return null;
    }

    void updateUser(Authentication.User u) throws DataAccessException {}
}
