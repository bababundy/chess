package dataaccess.mySQL;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySQLAuthDaoTest {
    private MySQLAuthDao dao;
    private final AuthData testAuth = new AuthData("testToken123", "testUser");

    @BeforeEach
    public void setup() throws DataAccessException {
        dao = new MySQLAuthDao();
        dao.clear();
        dao.createAuthUser(testAuth);
    }

    @Test
    void createAuthUser() {
        AuthData newAuth = new AuthData("newToken456", "newUser");
        try {
            dao.createAuthUser(newAuth);
        } catch (DataAccessException e) {
            throw new RuntimeException("Unexpected failure");
        }
        AuthData retrieved = null;
        try {
            retrieved = dao.getByToken("newToken456");
        } catch (DataAccessException e) {
            throw new RuntimeException("Unexpected failure");
        }
        assertEquals("newUser", retrieved.username());
    }

    @Test
    public void testCreateAuthUserFailsWithDuplicate() throws DataAccessException {
        AuthData auth = new AuthData("dupToken", "dupUser");
        dao.createAuthUser(auth);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.createAuthUser(auth);
        });
        assertTrue(ex.getMessage().contains("already taken"), "Should fail on duplicate token");
    }

    @Test
    void getByToken() {
        AuthData result = null;
        try {
            result = dao.getByToken("testToken123");
        } catch (DataAccessException e) {
            throw new RuntimeException("Unexpected failure");
        }
        assertEquals("testUser", result.username());
    }

    @Test
    public void testGetByTokenFailsWhenNotFound() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.getByToken("nonexistentToken");
        });

        assertTrue(ex.getMessage().contains("user not found"), "Should throw when token is missing");
    }

    @Test
    void getByUsername() {
        AuthData result = null;
        try {
            result = dao.getByUsername("testUser");
        } catch (DataAccessException e) {
            throw new RuntimeException("unexpected Failure");
        }
        assertEquals("testToken123", result.authToken());
    }

    @Test
    public void testGetByUsernameFailsWhenNotFound() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.getByUsername("ghostUser");
        });

        assertTrue(ex.getMessage().contains("user not found"), "Should throw when username is missing");
    }

    @Test
    void deleteAuth() {
        try {
            dao.deleteAuth("testToken123");
        } catch (DataAccessException e) {
            throw new RuntimeException("Unexpected Failure");
        }
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.getByToken("testToken123");
        });
        assertTrue(ex.getMessage().contains("user not found"));
    }

    @Test
    void clear() {
        try {
            dao.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException("Unexpected failure");
        }
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.getByToken("testToken123");
        });
        assertTrue(ex.getMessage().contains("user not found"));
    }
}