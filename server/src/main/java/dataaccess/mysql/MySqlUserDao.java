package dataaccess.mysql;

import dataaccess.*;
import dataaccess.daointerfaces.UserDAO;
import model.UserData;

import java.util.HashMap;
import java.util.List;

public class MySqlUserDao extends SqlDaoHelper implements UserDAO {
    @Override
    public void createUser(UserData newUser) throws DataAccessException {
        if(newUser.username() == null || newUser.password() == null || newUser.email() == null) {
            throw new DataAccessException("bad request");
        }
        //upload to database
        var statement = "INSERT INTO users (username, hashedPassword, email) VALUES (?, ?, ?)";
        executeUpdate(statement, newUser.username(), newUser.password(), newUser.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if(username == null){
            throw new DataAccessException("bad request");
        }
        var statement = "SELECT username, hashedPassword, email FROM users WHERE username = ?";
        List<HashMap<String, Object>> rows;
        try {
            rows = executeQuery(statement, username);
        } catch (Exception e) {
            throw new DataAccessException("500 Database failure in getUser", e);
        }

        if (rows.isEmpty()) {
            throw new DataAccessException("user not found");
        }
        HashMap<String, Object> row = rows.getFirst();
        return new UserData(
                (String) row.get("username"),
                (String) row.get("hashedPassword"),
                (String) row.get("email")
        );
    }

        @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }
}
