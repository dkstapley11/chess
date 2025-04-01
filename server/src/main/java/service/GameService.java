package service;
import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import model.AuthData;
import model.GameData;
import model.GameResponse;
import model.JoinRequest;
import model.GameListResponse;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {
    AuthDAO aDAO;
    GameDAO gDAO;

    public GameService(AuthDAO aDAO, GameDAO gDAO) {
        this.aDAO = aDAO;
        this.gDAO = gDAO;
    }

    public GameResponse createGame(String gameName, String authToken) throws ResponseException {

        if (aDAO.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: Invalid authentication token");
        }

        int gameID;
        do { // Get random gameIDs until the gameID is not already in use
            gameID = ThreadLocalRandom.current().nextInt(1, 10000);
        } while (gDAO.gameExists(gameID));

        try {
            ChessGame game = new ChessGame();
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            game.setBoard(board);
            gDAO.createGame(new GameData(gameID, null, null, gameName, game));
        } catch (ResponseException e) {
            throw new ResponseException(500, "could not create game");
        }
        GameResponse res;
        res = new GameResponse(gameID);
        return res;
    }

    public GameListResponse listGames(String authToken) throws ResponseException {
        if (aDAO.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: Invalid authentication token");
        }

        HashSet<GameData> games = gDAO.listGames();
        return new GameListResponse(games);
    }


    public boolean joinGame(String authToken, JoinRequest joinRequest) throws ResponseException {
        AuthData auth;
        GameData game;

        try {
            auth = aDAO.getAuth(authToken);
            if (auth == null) {
                throw new ResponseException(401, "Auth does not exist");
            }
        } catch (ResponseException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        try {
            game = gDAO.getGame(joinRequest.gameID());
            if (game == null) {
                throw new ResponseException(400, "Game id not found");
            }
        } catch (ResponseException e) {
            throw new ResponseException(400, "Error: A game with that ID doesn't exist");
        }

        String white = game.whiteUsername();
        String black = game.blackUsername();
        String username = auth.username();

        String playerColor = joinRequest.playerColor();
        if (playerColor != null) {
            playerColor = playerColor.toUpperCase(); // Normalize case
        } else {
            throw new ResponseException(400, "Error: bad request: null color provided");
        }

        // Validate the color choice
        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new ResponseException(400, "Error: bad request: Invalid player color. Choose 'WHITE' or 'BLACK'.");
        }

        // Check if the player can join the game
        if ("WHITE".equals(playerColor)) {
            if (white != null) {
                throw new ResponseException(403, "Error: White player slot is already taken.");
            }
            game = new GameData(game.gameID(), username, black, game.gameName(), game.game()); // Assign to white
        } else if ("BLACK".equals(playerColor)) {
            if (black != null) {
                throw new ResponseException(403, "Error: Black player slot is already taken.");
            }
            game = new GameData(game.gameID(), white, username, game.gameName(), game.game()); // Assign to black
        }

        try {
            gDAO.updateGame(game);
        } catch (ResponseException e) {
            throw new ResponseException(500, "Error: failed to update game");
        }
        return true;
    }

    public void updateGame(String authToken, GameData gameData) throws ResponseException {
        aDAO.getAuth(authToken);
        gDAO.updateGame(gameData);
    }

    public GameData getGameData(String authToken, int gameID) throws Exception {
        aDAO.getAuth(authToken);
        return gDAO.getGame(gameID);
    }

    public void clearGames() throws ResponseException {
        gDAO.clear();
    }
}
