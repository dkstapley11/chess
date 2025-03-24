package dataaccess;

import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    void createGame(GameData game)throws ResponseException;

    GameData getGame(int gameID) throws ResponseException;

    void updateGame(GameData game) throws ResponseException;

    void clear() throws ResponseException;

    boolean gameExists(int gameID) throws ResponseException;

    HashSet<GameData> listGames() throws ResponseException;

}
