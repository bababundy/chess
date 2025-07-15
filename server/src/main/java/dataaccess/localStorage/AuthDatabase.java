package dataaccess.localStorage;

import model.AuthData;

import java.util.ArrayList;
import java.util.Objects;

public class AuthDatabase {
    private static ArrayList<AuthData> authUsers = new ArrayList<>();

    public static void createAuthUser(AuthData user) {
        authUsers.add(user);
    }

    public static AuthData getUsername(String authToken) {
        for (AuthData user : authUsers) {
            if(Objects.equals(user.authToken(), authToken)) {
                return user;
            }
        }
        return null;
    }

    public static void deleteAuthUser(AuthData oldUser) {
        for (AuthData user : authUsers) {
            if(Objects.equals(user.authToken(), oldUser.authToken())) {
                authUsers.remove(user);
            }
        }
    }

    public void updateAuthUser(AuthData user){
        AuthData oldUser = getUsername(user.authToken());
        deleteAuthUser(oldUser);
        createAuthUser(user);
    }
}
