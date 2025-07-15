package service;

public class UserService {
    RegisterResult register(RegisterRequest req) {
        //1. verify input
        //2. check if username is already taken
        //3. create new user model object
        //4. insert new user into database UserDao.createUser(u)
        //5. login the new user (create new AuthToken model object, insert into database)
        //6. create registerresult and return
        return new RegisterResult(null, null, null);
    }

    LoginResult login(LoginRequest req) {
        //1. verify input
        //2. check if password is correct
        //3. database UserDao.getUser(u)
        //4. login the new user (create new AuthToken model object, insert into database)
        //5. create result and return
        return new LoginResult(null, null, null);
    }

    LogoutResult logout(LogoutRequest req) {
        //1. verify input
        //1.5 validate authToken
        //4. insert new user into database UserDao.createUser(u)
        //5. logout the new user (remove authToken model from database)
        //6. create result and return
        return new LogoutResult(null);
    }

    //create UUID authtoken method here
}


