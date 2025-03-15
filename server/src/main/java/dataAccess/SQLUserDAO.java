package dataAccess;

import model.UserData;
import org.mindrot.jbcrypt.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws ResponseException {
        configureUserDatabase();
    }

    @Override
    public void insertUser(UserData userData) throws ResponseException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, userData.username(), hashPassword(userData.password()), userData.email());
    }

    @Override
    public UserData getUser(String username) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var pw = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, pw, email);
    }

    @Override
    public boolean authenticateUser(String username, String password) throws ResponseException {
        return passwordMatches(password, username);
    };

    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean passwordMatches(String cleanPW, String username) throws ResponseException {
        var hashedPassword = getUserPassword(username);

        if (hashedPassword == null) {
            return false;  // Or throw an exception if needed
        }

        return BCrypt.checkpw(cleanPW, hashedPassword);
    }

    private String getUserPassword(String username) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("password");
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, "Error retrieving password: " + e.getMessage());
        }
        return null;
    }

    @Override
    public HashSet<UserData> listUsers() throws ResponseException {
        HashSet<UserData> users = new HashSet<>();
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT username, password, email FROM user");
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(readUser(rs));
            }
        } catch (SQLException e) {
            throw new ResponseException(500, "Unable to list users: " + e.getMessage());
        }
        return users;
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

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

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
