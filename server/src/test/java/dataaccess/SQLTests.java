package dataaccess;

import service.GameService;
import service.UserService;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class SQLTests {

    private GameService gameService;
    private UserService userService;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    private static UserData existingUser;

    private static UserData newUser;


    private String existingAuth;

    @BeforeEach
    public void setUp() {
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        System.out.println("Setup got run");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        try {
            gameService.clearGames();
            userService.clearUsers();
            AuthData authData = userService.registerUser(existingUser);
            existingAuth = authData.authToken();
            System.out.println("EXISTING AUTH: " + existingAuth);
        } catch (ResponseException e) {
            System.out.println("TRY BLOCK CAUSED AN EXCEPTION");
        }
    }
}

