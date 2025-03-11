package dataAccess;

import model.UserData;

import java.sql.SQLException;
import java.util.HashSet;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws ResponseException {
        configureUserDatabase();
    }

    @Override
    public void insertUser(UserData userData) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public HashSet<UserData> listUsers() {
        return null;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """
    };

    private void configureUserDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
