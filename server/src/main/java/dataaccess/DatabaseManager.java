package dataaccess;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DatabaseManager {
    private static final String DatabaseName;
    private static final String User;
    private static final String Password;
    private static final String ConnectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (InputStream in = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
                Properties props = new Properties();
                props.load(in);
                DatabaseName = props.getProperty("db.name");
                User = props.getProperty("db.user");
                Password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                ConnectionUrl = String.format("jdbc:mysql://%s:%d", host, port);

            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws ResponseException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DatabaseName;
            var conn = DriverManager.getConnection(ConnectionUrl, User, Password);
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws ResponseException {
        try {
            var conn = DriverManager.getConnection(ConnectionUrl, User, Password);
            conn.setCatalog(DatabaseName);
            return conn;
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}