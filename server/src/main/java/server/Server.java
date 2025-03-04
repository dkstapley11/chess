package server;

import dataAccess.*;
import spark.*;
import Service.GameService;
import Service.UserService;
import Service.AuthService;

public class Server {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    static UserService userService;
    static GameService gameService;
    static AuthService authService;

    UserHandler userHandler;
    GameHandler gameHandler;
    AuthHandler authHandler;

    public Server() {
        userDAO = new RamUserDAO();
        authDAO = new RamAuthDAO();
        gameDAO = new RamGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        new UserHandler().registerEndpoints();
        new AuthHandler().registerEndpoints();
        new GameHandler().registerEndpoints();


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
