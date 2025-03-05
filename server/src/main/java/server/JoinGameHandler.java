package server;

import Service.GameService;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.GameData;
import model.JoinRequest;
import spark.Request;
import spark.Response;

import java.util.HashSet;

public class JoinGameHandler {
    private GameService gameService;
    private Gson gson = new Gson();

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object joinGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }

            JoinRequest joinRequest = gson.fromJson(req.body(), JoinRequest.class);

            try {
                gameService.joinGame(authToken, joinRequest);
                res.status(200);
                return "{}"; // Success, no response body needed
            } catch (DataAccessException e) {
                String errorMessage = e.getMessage();

                if (errorMessage.contains("unauthorized")) {
                    res.status(401);
                } else if (errorMessage.contains("bad request") || errorMessage.contains("ID")) {
                    res.status(400);
                } else if (errorMessage.contains("already taken")) {
                    res.status(403);
                } else {
                    res.status(500); // Catch-all for unexpected server errors
                }

                return gson.toJson(new ErrorResponse(errorMessage));
            }

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
