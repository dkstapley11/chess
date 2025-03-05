package server;

import Service.GameService;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.GameResponse;
import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;

public class CreateGameHandler {

    private GameService gameService;
    private Gson gson = new Gson();

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object createGame(Request req, Response res) throws DataAccessException {
        try {
            // Parse request body into a helper object
            GameRequest gameRequest = gson.fromJson(req.body(), GameRequest.class);

            // Validate input
            if (gameRequest.gameName == null || gameRequest.gameName.isBlank()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request - missing gameName"));
            }

            String authToken = req.headers("authorization");

            try {
                GameResponse gameResponse = gameService.createGame(gameRequest.gameName, authToken);
                res.status(200);
                return gson.toJson(gameResponse);
            } catch (DataAccessException e) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private static class GameRequest {
        String gameName;
    }

}
