package server;

import Service.GameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Spark;

public class GameHandler {
    private GameService gameService;
    private Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void registerEndpoints() {
        Spark.post("/game", this::createGame);
        Spark.post("/game", this::listGames);
        Spark.post("/game", this::joinGame);
    }

    public Object createGame(Request req, Response res) {
        return null;
    }

    public Object listGames(Request req, Response res) {
        return null;
    }

    public Object joinGame(Request req, Response res) {
        return null;
    }
}
