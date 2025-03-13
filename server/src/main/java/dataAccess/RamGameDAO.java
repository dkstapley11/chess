package dataAccess;

import model.AuthData;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.HashSet;

public class RamGameDAO implements GameDAO {

    private HashSet<GameData> database;

    public RamGameDAO() {
        database = new HashSet<>(16);
    }

    @Override
    public void createGame(GameData game) {
        database.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws ResponseException {
        for (GameData game : database) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new ResponseException(400, "Game id not found: " +gameID);
    }

    @Override
    public void updateGame(GameData game) {
        try {
            database.remove(getGame(game.gameID()));
            database.add(game);
        } catch (ResponseException e) {
            database.add(game);
        }
    }

    @Override
    public void clear() {
        database.clear();
    }

    @Override
    public boolean gameExists(int gameID) {
        for (GameData game : database) {
            if (game.gameID() == gameID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public HashSet<GameData> listGames() {
        return new HashSet<>(database);
    }
}
