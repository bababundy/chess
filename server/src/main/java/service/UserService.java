package service;

import dataaccess.DataAccessException;
import dataaccess.localStorage.UserDatabase;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.LogoutResult;
import results.RegisterResult;
import dataaccess.*;

import java.util.UUID;

public class UserService {
    public static RegisterResult register(RegisterRequest req) {
        //1. verify input
        //2. check if username is already taken
        //3. create new user model object
        //4. insert new user into database UserDao.createUser(u)
        //5. login the new user (create new AuthToken model object, insert into database)
        //6. create registerresult and return
        return new RegisterResult(null, null, null);
    }

    public static LoginResult login(LoginRequest req) throws DataAccessException {
        String username = req.username();
        //1. verify input
        if (username == null || req.password() == null) {
            throw new DataAccessException("Missing username or password");
        }

        //2. database UserDao.getUser(u) and check if password is correct
        UserData user = UserDao.getUser(username);
        if (user == null || !user.password().equals(req.password())) {
            throw new DataAccessException("Invalid username or password");
        }

        //3.login the new user (create new AuthData model object, insert into database)
        String userAuthToken = UUID.randomUUID().toString();
        AuthData userAuthData = new AuthData(userAuthToken, username);
        AuthDao.createAuthUser(userAuthData);

        //4. create result and return
        return new LoginResult(username, userAuthToken, null);
    }

    public static LogoutResult logout(LogoutRequest req) {
        //1. verify input
        //1.5 validate authToken
        //4. insert new user into database UserDao.createUser(u)
        //5. logout the new user (remove authToken model from database)
        //6. create result and return
        return new LogoutResult(null);
    }

    //create UUID authtoken method here
}


