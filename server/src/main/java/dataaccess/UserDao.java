package dataaccess;

import dataaccess.localStorage.AuthDatabase;
import dataaccess.localStorage.UserDatabase;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

public class UserDao{
    private static UserDatabase db = new UserDatabase();


    public void createUser(UserData user) throws DataAccessException {
        UserDatabase.createUser(user);
    }

    public static UserData getUser(String username) throws DataAccessException {
        UserData user = db.getUser(username);
        if(user == null) {
            throw new DataAccessException("user not found");
        } else{
            return user;
        }
    }

    public void updateUser(Authentication.User u) throws DataAccessException {}
}
