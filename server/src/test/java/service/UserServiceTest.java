package service;

import dataaccess.*;
import dataaccess.daoInterfaces.AuthDAO;
import dataaccess.daoInterfaces.UserDAO;
import dataaccess.memory.MemoryAuthDao;
import dataaccess.memory.MemoryUserDao;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.*;
import results.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new MemoryUserDao();
        authDAO = new MemoryAuthDao();
        userService = new UserService(userDAO, authDAO);

        UserData user = new UserData("kolt", "password", "kolt@example.com");
        userDAO.createUser(user);
    }

    @Test
    void nullRegister() {
        RegisterRequest request = new RegisterRequest(null, null, null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertTrue(ex.getMessage().contains("Missing"));
    }

    @Test
    void usernameAlreadyTaken() {
        RegisterRequest request = new RegisterRequest("kolt", "password2", "kolt@example.com");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertTrue(ex.getMessage().contains("taken"));
    }

    @Test
    void validRegistration() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("kolt2", "password", "kolt@example.com");
        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertEquals("kolt2", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isBlank());

        assertNotNull(authDAO.getByUsername(result.username()));
    }

    @Test
    void nullLogin() {
        LoginRequest request = new LoginRequest(null, null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.login(request);
        });
        assertTrue(ex.getMessage().contains("Missing"));
    }

    @Test
    void invalidLogin() {
        LoginRequest request = new LoginRequest("kolt", "wrongpassword");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.login(request);
        });
        assertTrue(ex.getMessage().contains("Invalid"));
    }

    @Test
    void validLogin() throws DataAccessException {
        LoginRequest request = new LoginRequest("kolt", "password");
        LoginResult result = userService.login(request);

        assertNotNull(result);
        assertEquals("kolt", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isBlank());

        assertNotNull(authDAO.getByUsername(result.username()));
    }

    @Test
    void invalidLogout() {
        LogoutRequest request = new LogoutRequest("abcd124");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.logout(request);
        });
        assertTrue(ex.getMessage().contains("Invalid"));
    }

    @Test
    void nullLogout() {
        LogoutRequest request = new LogoutRequest(null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.logout(request);
        });
        assertTrue(ex.getMessage().contains("Missing"));
    }

    @Test
    void validLogout() throws DataAccessException {
        String authToken = "a17f1235-56d3-4cb9-81c8-9237bc5ab3ec";
        authDAO.createAuthUser(new AuthData(authToken, "kolt"));
        LogoutRequest request = new LogoutRequest(authToken);
        LogoutResult result = userService.logout(request);

        assertNull(result.message());

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            authDAO.getByToken(authToken);
        });
        assertTrue(ex.getMessage().contains("Invalid"));
    }
}
