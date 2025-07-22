package dataaccess.mySQL;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class sqlDaoHelper {
    protected void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("500 Unable to update database: " + e.getMessage(), e);
        }
    }

    protected List<HashMap<String, Object>> executeQuery(String statement, Object... params) throws DataAccessException {
        List<HashMap<String, Object>> results = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    throw new SQLException("Unsupported parameter type: " + param.getClass());
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                while (rs.next()) {
                    HashMap<String, Object> row = new HashMap<>();
                    for (int col = 1; col <= columnCount; col++) {
                        String columnName = meta.getColumnLabel(col);
                        Object value = rs.getObject(col);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("500 Error executing query: " + e.getMessage(), e);
        }
        return results;
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS users (
          username VARCHAR(256) NOT NULL PRIMARY KEY,
          hashedPassword VARCHAR(256) NOT NULL,
          email VARCHAR(256) NOT NULL,
          INDEX(username),
          INDEX(email)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS authUsers (
            authToken VARCHAR(256) NOT NULL PRIMARY KEY,
            username VARCHAR(256) NOT NULL,
            INDEX(username)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS games (
            gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            gameName VARCHAR(256) NOT NULL,
            whiteUsername VARCHAR(256),
            blackUsername VARCHAR(256),
            gameJson TEXT,
            INDEX(gameName),
            INDEX(whiteUsername),
            INDEX(blackUsername)
        )
        """
    };

    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("500 Unable to configure database: %s", ex.getMessage()));
        }
    }
}
