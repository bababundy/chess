package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.*;
import results.LoginResult;
import results.LogoutResult;
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

        assertNotNull(AuthDao.getAuthUser(result.username()));
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

        assertNotNull(AuthDao.getAuthUser(result.username()));
    }

    @Test
    void invalidLogout() throws DataAccessException {
        LogoutRequest request = new LogoutRequest("abcd1234");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            UserService.logout(request);
        });
        assertTrue(ex.getMessage().contains("Invalid"));
    }

    @Test
    void nullLogout() throws DataAccessException {
        LogoutRequest request = new LogoutRequest(null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            UserService.logout(request);
        });
        assertTrue(ex.getMessage().contains("Missing"));
    }

    @Test
    void validLogout() throws DataAccessException {
        LoginRequest request = new LoginRequest("kolt", "password");
        LoginResult loginResult = UserService.login(request);
        LogoutResult result = UserService.logout(new LogoutRequest(loginResult.authToken()));

        assertNull(result.message());
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            AuthDao.getAuthUser("kolt");
        });
        assertTrue(ex.getMessage().contains("not found"));
    }
}