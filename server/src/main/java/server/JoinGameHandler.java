package server;

import service.GameService;
import com.google.gson.Gson;
import dataaccess.ResponseException;
import model.JoinRequest;
import spark.Request;
import spark.Response;

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
            } catch (ResponseException e) {
                String errorMessage = e.getMessage();
                int code = e.statusCode();
                res.status(code);

                return gson.toJson(new ErrorResponse(errorMessage));
            }

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
