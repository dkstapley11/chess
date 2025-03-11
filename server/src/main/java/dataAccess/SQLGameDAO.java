package dataAccess;

import model.GameData;

import java.util.HashSet;

public class SQLGameDAO implements GameDAO {
    @Override
    public void createGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean gameExists(int gameID) {
        return false;
    }

    @Override
    public HashSet<GameData> listGames() {
        return null;
    }
}
