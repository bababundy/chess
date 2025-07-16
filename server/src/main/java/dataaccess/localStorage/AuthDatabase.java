package dataaccess.localStorage;

import dataaccess.DataAccessException;
import model.AuthData;

import java.util.ArrayList;
import java.util.Objects;

public class AuthDatabase {
    private static ArrayList<AuthData> authUsers = new ArrayList<>();

    public static void createAuthUser(AuthData user) {
        authUsers.add(user);
    }

    public static AuthData getByToken(String authToken) throws DataAccessException {
        for (AuthData user : authUsers) {
            if(Objects.equals(user.authToken(), authToken)) {
                return user;
            }
        }
        throw new DataAccessException("user not found");
    }

    public static AuthData getByUsername(String username) throws DataAccessException {
        for (AuthData user : authUsers) {
            if(Objects.equals(user.username(), username)) {
                return user;
            }
        }
        throw new DataAccessException("user not found");
    }

    public static void deleteAuthUser(AuthData oldUser) {
        authUsers.remove(oldUser);
    }

    public void updateAuthUser(AuthData user){
//        AuthData oldUser = getByUsername(user.username());
//        deleteAuthUser(oldUser);
//        createAuthUser(user);
    }
}
