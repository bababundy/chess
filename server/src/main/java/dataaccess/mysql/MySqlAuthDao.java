package dataaccess.mysql;

import dataaccess.daointerfaces.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;
import java.util.List;

public class MySqlAuthDao extends SqlDaoHelper implements AuthDAO {
    
    public MySqlAuthDao() throws DataAccessException {
    }

    @Override
    public void createAuthUser(AuthData auth) throws DataAccessException {
        //ensure no duplicate keys
        AuthData previousUser = null;
        try{
            previousUser = getByToken(auth.authToken());
            throw new DataAccessException("already taken");
        } catch (DataAccessException e){
            if(previousUser != null){
                throw new DataAccessException("already taken");
            }
        }
        //upload to database
        var statement = "INSERT INTO authUsers (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, auth.username(), auth.authToken());
    }

    @Override
    public AuthData getByToken(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM authUsers WHERE authToken = ?";
        List<HashMap<String, Object>> rows;

        try {
            rows = executeQuery(statement, authToken);
        } catch (Exception e) {
            throw new DataAccessException("500 Database failure in getUser", e);
        }

        if (rows.isEmpty()) {
            throw new DataAccessException("user not found");
        }
        HashMap<String, Object> row = rows.getFirst();
        return new AuthData(
            (String) row.get("authToken"),
            (String) row.get("username")
        );
    }

    @Override
    public AuthData getByUsername(String username) throws DataAccessException {
        var statement = "SELECT authToken, username FROM authUsers WHERE username = ?";
        List<HashMap<String, Object>> rows = executeQuery(statement, username);

        if (rows.isEmpty()) {
            throw new DataAccessException("user not found");
        }
        HashMap<String, Object> row = rows.getFirst();
        return new AuthData(
            (String) row.get("authToken"),
            (String) row.get("username")
        );
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM authUsers WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE authUsers";
        executeUpdate(statement);
    }
}
