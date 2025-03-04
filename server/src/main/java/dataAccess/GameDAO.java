package dataAccess;

import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    void createGame(GameData game)throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void clear();

    boolean gameExists(int gameID);

    HashSet<GameData> listGames();

}
