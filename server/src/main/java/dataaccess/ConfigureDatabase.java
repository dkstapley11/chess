package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class ConfigureDatabase {
    /**
     * Execute each SQL statement in the provided list,
     * wrapping any SQLException in a ResponseException.
     */
    public static void configure(String[] createStatements) throws ResponseException {
        DatabaseManager.createDatabase();

        // open a connection and run all statements
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String sql : createStatements) {
                try (var ps = conn.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(
                    500,
                    String.format("Unable to configure database: %s", ex.getMessage())
            );
        }
    }
}

