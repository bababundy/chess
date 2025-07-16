package dataaccess;

import dataaccess.localStorage.AuthDatabase;
import model.AuthData;

public class AuthDao {
    private static AuthDatabase db = new AuthDatabase();

    public static void createAuthUser(AuthData user) throws DataAccessException {
        db.createAuthUser(user);
    }

    public static AuthData getAuthUser(String username) throws DataAccessException {
        AuthData user = db.getByUsername(username);
        if(user == null) {
            throw new DataAccessException("user not found");
        } else{
            return user;
        }
    }

    public void updateAuthUser(AuthData user) throws DataAccessException {}

    public String getUsername (String authToken) throws DataAccessException {
        return db.getByToken(authToken).username();
    }

    public String getAuthToken (String username) throws DataAccessException {
        return db.getByUsername(username).authToken();
    }

    public void deleteAuthUser(AuthData user) {
        db.deleteAuthUser(user);
    }
}