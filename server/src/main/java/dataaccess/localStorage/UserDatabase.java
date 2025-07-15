package dataaccess.localStorage;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class UserDatabase {
    private static ArrayList<UserData> users = new ArrayList<>();

    public static void createUser(UserData user) {
        users.add(user);
    }

    public static UserData getUser(String username) {
        for (UserData user : users) {
            if(Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }
}
