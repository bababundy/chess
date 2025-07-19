package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import requests.*;
import results.*;
import dataaccess.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        String username = req.username();
        String password = req.password();
        String email = req.email();

        //1. verify input
        if (username == null || password == null || email == null) {
            throw new DataAccessException("Missing input field");
        }

        //2. check if username is already taken
        try {
            UserData user = userDAO.getUser(username);
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

        //4. login the new user (create new AuthToken model object, insert into database)
        var result = login(new LoginRequest(username, password));

        //5. create registerResult and return
        return new RegisterResult(result.username(), result.authToken(), null);
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        String username = req.username();
        //1. verify input
        if (username == null || req.password() == null) {
            throw new DataAccessException("Missing username or password");
        }

        //2. check if password is correct
        UserData user = userDAO.getUser(username);
        if (!user.password().equals(req.password())) {
            throw new DataAccessException("Invalid username or password");
        }

        //3.login the new user (create new AuthData model object, insert into database)
        String userAuthToken = UUID.randomUUID().toString();
        AuthData userAuthData = new AuthData(userAuthToken, username);
        authDAO.createAuthUser(userAuthData);

        //4. create result and return
        return new LoginResult(username, userAuthToken, null);
    }

    public LogoutResult logout(LogoutRequest req) throws DataAccessException {
        //1. verify input
        String authToken = req.authToken();
        if (authToken == null) {
            throw new DataAccessException("Missing authToken");
        }

        //2. validate authToken

        try{
            authDAO.getByToken(authToken);}
        catch (DataAccessException e) {
            throw new DataAccessException("Invalid AuthToken");
        }

        //3. logout the new user (remove authToken model from database)
        authDAO.deleteAuth(authToken);

        //4. create result and return
        return new LogoutResult(null);
    }
}