package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import static dataaccess.ConfigureDatabase.configure;
import static dataaccess.DataUpdate.executeUpdate;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws ResponseException {
        String[] createStatements = {
                """            
            CREATE TABLE if NOT EXISTS auth (
            username VARCHAR(255) NOT NULL,
            authToken VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
            )"""
        };
        configure(createStatements);
    }

    @Override
    public void createAuth(AuthData auth) throws ResponseException {
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, auth.username(), auth.authToken());
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var token = rs.getString("authToken");
        return new AuthData(username, token);
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        int result = executeUpdate(statement, authToken);
        if (result == 0) {
            throw new ResponseException(500, "auth did not exist");
        }
    }

    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public HashSet<AuthData> listAuths() throws ResponseException {

        HashSet<AuthData> users = new HashSet<>();
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT username, authToken FROM auth");
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(readAuth(rs));
            }
        } catch (SQLException e) {
            throw new ResponseException(500, "Unable to list users: " + e.getMessage());
        }
        return users;
    }
}
