package dataAccess;

import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    void createGame(GameData game)throws ResponseException;

    GameData getGame(int gameID) throws ResponseException;

    void updateGame(GameData game) throws ResponseException;

    void clear();

    boolean gameExists(int gameID);

    HashSet<GameData> listGames();

}
