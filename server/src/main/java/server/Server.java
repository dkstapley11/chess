package server;

import dataaccess.*;
import spark.*;
import service.GameService;
import service.UserService;

public class Server {

    private final CreateGameHandler createGameHandler;
    private final RegisterHandler registerHandler;
    private final JoinGameHandler joinGameHandler;
    private final ListGamesHandler listGamesHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;

    UserService userService;
    GameService gameService;

    public Server() {
        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;
        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }



        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);

        registerHandler = new RegisterHandler(userService);
        createGameHandler = new CreateGameHandler(gameService);
        joinGameHandler = new JoinGameHandler(gameService);
        listGamesHandler = new ListGamesHandler(gameService);
        logoutHandler = new LogoutHandler(userService);
        loginHandler = new LoginHandler(userService);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", registerHandler::register);
        Spark.get("/game", listGamesHandler::listGames);
        Spark.post("/game", createGameHandler::createGame);
        Spark.put("/game", joinGameHandler::joinGame);
        Spark.delete("/db", this::clear);
        Spark.delete("/session", logoutHandler::logout);
        Spark.post("/session", loginHandler::login);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clear(Request req, Response res) throws ResponseException {
        userService.clearUsers();
        gameService.clearGames();
        res.status(200);
        return "{}";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
