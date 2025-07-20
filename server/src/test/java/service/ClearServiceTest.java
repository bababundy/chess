package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.memory.MemoryAuthDao;
import dataaccess.memory.MemoryGameDao;
import dataaccess.memory.MemoryUserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.ClearResult;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private ClearService clearService;

    @BeforeEach
    void setUp() throws DataAccessException {
        DAOFacade.authDAO = new MemoryAuthDao();
        DAOFacade.userDAO = new MemoryUserDao();
        DAOFacade.gameDAO = new MemoryGameDao();

        DAOFacade.authDAO.createAuthUser(new AuthData("abcd1234", "kolt"));
        DAOFacade.userDAO.createUser(new UserData("kolt", "password", "kolt@example.com"));
        DAOFacade.gameDAO.createGame(new GameData(1, "white", "black", "cool game", new ChessGame()));

        clearService = new ClearService(DAOFacade.userDAO, DAOFacade.gameDAO, DAOFacade.authDAO);
    }

    @Test
    void clear() throws Exception {
        ClearResult result = clearService.clear();

        assertThrows(DataAccessException.class, () -> DAOFacade.authDAO.getByToken("abcd1234"));
        assertThrows(DataAccessException.class, () -> DAOFacade.userDAO.getUser("kolt"));
        assertTrue(DAOFacade.gameDAO.getList().isEmpty());
    }
}
