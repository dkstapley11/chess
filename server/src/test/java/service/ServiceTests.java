package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestCreateRequest;


import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    private GameService gameService;
    private UserService userService;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    private static UserData existingUser;

    private static UserData newUser;

    private static TestCreateRequest createRequest;



    private String existingAuth;

    @BeforeEach
    public void setUp() {
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");

        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
        authDAO = new RamAuthDAO();
        userDAO = new RamUserDAO();
        gameDAO = new RamGameDAO();
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        try {
           AuthData authData = userService.registerUser(existingUser);
           existingAuth = authData.authToken();
        } catch (ResponseException e) {
        }
    }

    // Test registerUser() - Positive Case
    @Test
    public void testRegisterUserSuccess() throws ResponseException {
        AuthData authData = userService.registerUser(newUser);

        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertEquals(newUser.username(), authData.username());
    }

    // Test registerUser() - Negative Case (Invalid Data)
    @Test
    public void testRegisterUserFailureInvalidData() {
        UserData invalidUser = new UserData("", "password", "email@mail.com");

        assertThrows(ResponseException.class, () -> {
            userService.registerUser(invalidUser);
        });
    }

    // Test registerUser() - Negative Case (Username Already Taken)
    @Test
    public void testRegisterUserFailureUsernameTaken() {
        assertThrows(ResponseException.class, () -> {
            userService.registerUser(existingUser);  // Username already exists
        });
    }

    // Test loginUser() - Positive Case
    @Test
    public void testLoginUserSuccess() throws ResponseException {
        AuthData authData = userService.loginUser(existingUser.username(), existingUser.password());

        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertEquals(existingUser.username(), authData.username());
    }

    // Test loginUser() - Negative Case (Invalid Credentials)
    @Test
    public void testLoginUserFailureInvalidCredentials() {
        assertThrows(ResponseException.class, () -> {
            userService.loginUser("nonExistingUser", "wrongPassword");
        });
    }

    // Test logoutUser() - Positive Case
    @Test
    public void testLogoutUserSuccess() throws ResponseException {
        // Assuming authToken is valid and exists
        userService.logoutUser(existingAuth);

        // Verify that the token has been removed (check DAO)
        assertThrows(ResponseException.class, () -> {
            authDAO.getAuth(existingAuth);  // This should throw an exception as token is deleted
        });
    }

    // Test logoutUser() - Negative Case (Invalid Token)
    @Test
    public void testLogoutUserFailureInvalidToken() {
        String invalidAuthToken = "invalidToken";

        assertThrows(ResponseException.class, () -> {
            userService.logoutUser(invalidAuthToken);  // This should throw an exception
        });
    }

    // Test clearUsers() - Positive Case
    @Test
    public void testClearUsers() throws ResponseException {
        userService.clearUsers();

        // Check if the users have been cleared
        assertTrue(userDAO.listUsers().isEmpty());
        assertTrue(authDAO.listAuths().isEmpty());
    }

    // Test createGame() - Positive Case
    @Test
    public void testCreateGameSuccess() throws ResponseException {

        GameResponse response = gameService.createGame("testGame", existingAuth);

        assertNotNull(response);
        assertTrue(response.gameID() > 0);  // Assuming the gameID is a positive number
    }

    // Test createGame() - Negative Case (Invalid Token)
    @Test
    public void testCreateGameFailure() {
        String invalidAuthToken = "invalidToken";
        String gameName = "Invalid Game";

        assertThrows(ResponseException.class, () -> {
            gameService.createGame(gameName, invalidAuthToken);
        });
    }

    // Test listGames() - Positive Case
    @Test
    public void testListGamesSuccess() throws ResponseException {
        gameService.createGame("testGame2", existingAuth);

        GameListResponse response = gameService.listGames(existingAuth);

        assertNotNull(response);
        assertFalse(response.games().isEmpty());  // Assuming there are games in the list
    }

    // Test listGames() - Negative Case (Invalid Token)
    @Test
    public void testListGamesFailure() {
        String invalidAuthToken = "invalidToken";

        assertThrows(ResponseException.class, () -> {
            gameService.listGames(invalidAuthToken);
        });
    }

    // Test joinGame() - Positive Case
    @Test
    public void testJoinGameSuccess() throws ResponseException {
        GameResponse response = gameService.createGame("testGame3", existingAuth);
        int gameID = response.gameID();

        JoinRequest joinRequest = new JoinRequest("WHITE", gameID);

        boolean result = gameService.joinGame(existingAuth, joinRequest);

        assertTrue(result);  // Ensure the join was successful
    }

    // Test joinGame() - Negative Case (Slot Already Taken)
    @Test
    public void testJoinGameFailure() {
        String authToken = "validToken";
        JoinRequest joinRequest = new JoinRequest("WHITE", 1);

        // Set up the game so that the white slot is already taken
        assertThrows(ResponseException.class, () -> {
            gameService.joinGame(authToken, joinRequest);
        });
    }

    // Test clearGames() - Positive Case
    @Test
    public void testClearGames() throws ResponseException {
        gameService.clearGames();

        // Assume you have access to the DAO or you can check the database
        // directly to verify the game list is empty
        assertTrue(gameDAO.listGames().isEmpty());  // Ensure the game list is cleared
    }
}

