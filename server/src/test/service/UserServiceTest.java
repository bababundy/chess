package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import dataaccess.localStorage.AuthDatabase;
import dataaccess.localStorage.UserDatabase;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.*;
import results.*;
import results.LoginResult;
import server.Server;

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