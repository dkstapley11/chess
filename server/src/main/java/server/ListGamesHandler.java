package server;

import Service.GameService;
import com.google.gson.Gson;
import model.GameListResponse;
import spark.Request;
import spark.Response;

import java.util.HashSet;

public class ListGamesHandler {
    private GameService gameService;
    private Gson gson = new Gson();

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }

            GameListResponse games = gameService.listGames(authToken);
            res.status(200);
            return gson.toJson(games);

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
