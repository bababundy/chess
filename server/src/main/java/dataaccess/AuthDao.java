package dataaccess;

import dataaccess.localStorage.AuthDatabase;
import model.AuthData;
import org.eclipse.jetty.server.Authentication;

public class AuthDao {
    private static AuthDatabase db = new AuthDatabase();

    public static void createAuthUser(AuthData user) throws DataAccessException {
        AuthDatabase.createAuthUser(user);
    }

    public static AuthData getAuthUser(String username) throws DataAccessException {
        AuthData user = AuthDatabase.getUsername(username);
        if(user == null) {
            throw new DataAccessException("user not found");
        } else{
            return user;
        }
    }

    public void updateAuthUser(Authentication.User u) throws DataAccessException {}
}