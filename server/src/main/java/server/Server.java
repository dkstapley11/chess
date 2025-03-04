package server;

import dataAccess.*;
import model.UserData;
import spark.*;
import Service.GameService;
import Service.UserService;
import Service.AuthService;

public class Server {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    private UserService userService;
    private GameService gameService;
    private AuthService authService;


    private GameHandler gameHandler;
    private RegisterHandler registerHandler;

    public Server() {
        userDAO = new RamUserDAO();
        authDAO = new RamAuthDAO();
        gameDAO = new RamGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);

        registerHandler = new RegisterHandler(userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", registerHandler::register);

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
