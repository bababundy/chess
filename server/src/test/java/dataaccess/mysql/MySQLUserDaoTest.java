package dataaccess.mysql;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySQLUserDaoTest {
    private MySqlUserDao userDao;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDao = new MySqlUserDao();
        userDao.clear();
    }

    @Test
    void createUser() {
        UserData user = new UserData("kolt", "hashedPass123", "test@example.com");
        assertDoesNotThrow(() -> userDao.createUser(user));
        try {
            UserData result = userDao.getUser("kolt");
            assertNotNull(result);
            assertEquals(user.username(), result.username());
            assertEquals(user.password(), result.password());
            assertEquals(user.email(), result.email());
        } catch (DataAccessException e) {
            fail("getUser threw unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testGetUserFailsWhenNotFound() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userDao.getUser("ghostUser");
        });

        assertTrue(ex.getMessage().toLowerCase().contains("user not found"));
    }

    @Test
    public void testGetUserFailsWhenNullUsername() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userDao.getUser(null);
        });

        assertTrue(ex.getMessage().toLowerCase().contains("bad") || ex.getMessage().toLowerCase().contains("request"),
                "Expected an error message related to null input");
    }

    @Test
    public void testCreateUserFailsWhenNullUsername() {
        UserData badUser = new UserData(null, "somePassword", "fail@example.com");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userDao.createUser(badUser);
        });

        assertTrue(ex.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    void clear() {
        UserData user = new UserData("clearUser", "pass", "clear@example.com");
        try {
            userDao.createUser(user);
        } catch (DataAccessException e) {
            throw new RuntimeException("unexpected Failure");
        }
        assertDoesNotThrow(() -> userDao.getUser("clearUser"), "User should exist before clear");
        try {
            userDao.clear();
        } catch (DataAccessException e) {
            fail("Clear threw exception: " + e.getMessage());
        }
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userDao.getUser("clearUser");
        });
        assertTrue(ex.getMessage().contains("user not found"));
    }
}