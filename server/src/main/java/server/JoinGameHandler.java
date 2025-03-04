package server;

import Service.GameService;
import com.google.gson.Gson;
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
            if (joinRequest == null || joinRequest.playerColor() == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request - missing playerColor or gameID"));
            }
            boolean joined = gameService.joinGame(authToken, joinRequest);
            if (!joined) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: could not join game"));
            }

            res.status(200);
            return "{}";

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
