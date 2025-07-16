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

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private static UserDao userDAO = new UserDao();
    private static AuthDao authDAO = new AuthDao();

    public static RegisterResult register(RegisterRequest req) throws DataAccessException {
        String username = req.username();
        String password = req.password();
        String email = req.email();

        //1. verify input
        if (username == null || password == null || email == null) {
            throw new DataAccessException("Missing input field");
        }

        //2. check if username is already taken
        try {
            UserData user = UserDao.getUser(username);
            throw new DataAccessException("Username already taken");
        } catch(DataAccessException e) {
            if(Objects.equals(e.getMessage(), "user not found")){
                //username available
            } else{
                throw new DataAccessException("Username already taken");
            }
        }

        //3. create new user model object insert new user into database
        userDAO.createUser(new UserData(username, password, email));

        //5. login the new user (create new AuthToken model object, insert into database)
        var result = login(new LoginRequest(username, password));

        //6. create registerresult and return
        return new RegisterResult(result.username(), result.authToken(), null);
    }

    public static LoginResult login(LoginRequest req) throws DataAccessException {
        String username = req.username();
        //1. verify input
        if (username == null || req.password() == null) {
            throw new DataAccessException("Missing username or password");
        }

        //2. check if password is correct
        UserData user = UserDao.getUser(username);
        if (!user.password().equals(req.password())) {
            throw new DataAccessException("Invalid username or password");
        }

        //3.login the new user (create new AuthData model object, insert into database)
        String userAuthToken = UUID.randomUUID().toString();
        AuthData userAuthData = new AuthData(userAuthToken, username);
        AuthDao.createAuthUser(userAuthData);

        //4. create result and return
        return new LoginResult(username, userAuthToken, null);
    }

    public static LogoutResult logout(LogoutRequest req) throws DataAccessException {
        //1. verify input
        String authToken = req.authToken();
        if (authToken == null) {
            throw new DataAccessException("Missing authToken");
        }
        //2. validate authToken
        String username;
        try{username = authDAO.getUsername(authToken);}
        catch (DataAccessException e) {
            throw new DataAccessException("Invalid AuthToken");
        }
        //3. logout the new user (remove authToken model from database)
        authDAO.deleteAuthUser(new AuthData(authToken, username));


        //4. create result and return
        return new LogoutResult(null);
    }
}