package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.*;
import results.LoginResult;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserDao userdao;
    private AuthDao authdao;

    @BeforeEach
    void setup() throws DataAccessException {
        userdao = new UserDao();
        authdao = new AuthDao();
        UserData user = new UserData("kolt", "password", "kolt@example.com");
        userdao.createUser(user);
    }

    @Test
    void nullRegister() throws DataAccessException {
        RegisterRequest request = new RegisterRequest(null, null, null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            UserService.register(request);
        });
        assertTrue(ex.getMessage().contains("Missing"));
    }

    @Test
    void usernameAlreadyTaken() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("kolt", "password2", "kolt@example.com");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            UserService.register(request);
        });
        assertTrue(ex.getMessage().contains("taken"));
    }

    @Test
    void validRegistration() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("kolt2", "password", "kolt@example.com");
        RegisterResult result = UserService.register(request);

        assertNotNull(result);
        assertEquals("kolt2", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isBlank());

        assertNotNull(AuthDao.getAuthUser(result.authToken()));
    }

    @Test
    void nullLogin() throws DataAccessException {
        LoginRequest request = new LoginRequest(null, null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            UserService.login(request);
        });
        assertTrue(ex.getMessage().contains("Missing"));
    }

    @Test
    void invalidLogin() throws DataAccessException {
        LoginRequest request = new LoginRequest("kolt", "wrongpassword");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            UserService.login(request);
        });
        assertTrue(ex.getMessage().contains("Invalid"));
    }

    @Test
    void validLogin() throws DataAccessException {
        LoginRequest request = new LoginRequest("kolt", "password");
        LoginResult result = UserService.login(request);

        assertNotNull(result);
        assertEquals("kolt", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isBlank());

        assertNotNull(AuthDao.getAuthUser(result.authToken()));
    }


}