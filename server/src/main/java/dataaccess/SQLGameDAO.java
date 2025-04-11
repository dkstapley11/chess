package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws ResponseException {
        configureGameDatabase();
    }

    @Override
    public void createGame(GameData game) throws ResponseException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), serializeGame(game.game()));
    }

    @Override
    public GameData getGame(int gameID) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var white = rs.getString("whiteUsername");
        var black = rs.getString("blackUsername");
        var name = rs.getString("gameName");
        var game = rs.getString("chessGame");
        ChessGame chessGame = deserializeGame(game);
        return new GameData(gameID, white, black, name, chessGame);
    }

    public void removePlayer(String color, int gameID) throws ResponseException {
        if (color == "WHITE") {
            var statement = "UPDATE game SET whiteUsername=null WHERE gameID=?";
            executeUpdate(statement, gameID);
        }
        if (color == "BLACK") {
            var statement = "UPDATE game SET blackUsername=null WHERE gameID=?";
            executeUpdate(statement, gameID);
        }
    }

    @Override
    public void updateGame(GameData game) throws ResponseException {
        var statement = "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?";
        String textGame = serializeGame(game.game());
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), textGame, game.gameID());
    }

    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    public boolean gameExists(int gameID) throws ResponseException {

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT 1 FROM game WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, "Error checking game existence: " + e.getMessage());
        }
    }

    @Override
    public HashSet<GameData> listGames() throws ResponseException {
        var games = new HashSet<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (var ps = conn.prepareStatement(statement);
                 var rs = ps.executeQuery()) {
                while (rs.next()) {
                    games.add(readGame(rs));
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, "Unable to retrieve games: " + e.getMessage());
        }
        return games;
    }

    @Override
    public void removePlayer(int gameID, String color) throws ResponseException {
        if (color.equals("white")) {
            var statement = "UPDATE game SET whiteUsername=null WHERE gameID=?";
            executeUpdate(statement, gameID);
        }
        if (color.equals("black")) {
            var statement = "UPDATE game SET blackUsername=null WHERE gameID=?";
            executeUpdate(statement, gameID);
        }
    }

    private String serializeGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame deserializeGame(String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
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

    private final String[] createStatements = {
            """            
            CREATE TABLE if NOT EXISTS game (
            gameID INT NOT NULL,
            whiteUsername VARCHAR(256),
            blackUsername VARCHAR(256),
            gameName VARCHAR(256),
            chessGame TEXT,
            PRIMARY KEY (gameID)
            )
            """
    };

    private void configureGameDatabase() throws ResponseException {
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
