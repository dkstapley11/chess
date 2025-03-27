package client;

import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;

import ui.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import model.GameData;
import model.GameListResponse;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        // Start the test server on a random port.
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on port " + port);
        // Initialize the facade with the proper URL.
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws dataaccess.ResponseException {
        server.clearDB();
    }

    // ======= REGISTER =======

    @Test
    public void testRegisterPositive() throws Exception {
        // Create a new user and verify that the returned auth token is valid.
        UserData user = new UserData("player1", "password", "player1@example.com");
        AuthData auth = facade.register(user);
        assertNotNull(auth, "AuthData should not be null.");
        assertTrue(auth.authToken().length() > 10, "Auth token should be sufficiently long.");
    }

    @Test
    public void testRegisterNegative() {
        // Register a user, then attempt to register again with the same username.
        UserData user = new UserData("player2", "password", "player2@example.com");
        try {
            facade.register(user);
        } catch (ResponseException ex) {
            fail("Unexpected exception during first registration: " + ex.getMessage());
        }
        ResponseException ex = assertThrows(ResponseException.class, () -> {
            facade.register(user);
        });
    }

    // ======= LOGIN =======

    @Test
    public void testLoginPositive() throws Exception {
        // Register a user then attempt to login with correct credentials.
        UserData user = new UserData("player3", "password", "player3@example.com");
        facade.register(user);
        // The login call should succeed and not throw.
        facade.login("player3", "password");
        // You might indirectly verify this by a subsequent call that requires authentication.
        // For example, listGames() should now succeed.
        GameListResponse list = facade.listGames();
        assertNotNull(list);
    }

    @Test
    public void testLoginNegative() {
        // Attempt to login with non-existent credentials.
        ResponseException ex = assertThrows(ResponseException.class, () -> {
            facade.login("nonexistentUser", "wrongpassword");
        });
        // Check that the error message indicates unauthorized access.
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"),
                "Error should indicate wrong credentials or unauthorized access.");
    }

    // ======= LOGOUT =======

    @Test
    public void testLogoutPositive() throws Exception {
        // Register, login, then logout.
        UserData user = new UserData("player4", "password", "player4@example.com");
        facade.register(user);
        facade.login("player4", "password");
        // Logout should complete without error.
        facade.logout();
    }

    // ======= JOIN GAME =======

    @Test
    public void testJoinGamePositive() throws Exception {
        UserData user = new UserData("player3", "password", "player3@example.com");
        facade.register(user);
        facade.login("player3", "password");
        GameData createdGame = facade.createGame("TestGame");
        facade.joinGame("WHITE", createdGame.gameID());
        assertTrue(true);
    }

    @Test
    public void testJoinGameNegative() throws Exception {
        UserData user = new UserData("player3", "password", "player3@example.com");
        facade.register(user);
        // The login call should succeed and not throw.
        facade.login("player3", "password");
        // Create a game so that the list is not empty.
        facade.createGame("TestGameNegative");
        facade.listGames();
        // Attempt to join a game using an invalid game number (e.g., 999, which is out of range).
        ResponseException ex = assertThrows(ResponseException.class, () -> {
            facade.joinGame("WHITE", 1);
        });
    }

    @Test
    public void testCreateGameNegative() throws ResponseException {
        UserData user = new UserData("player3", "password", "player3@example.com");
        facade.register(user);
        // The login call should succeed and not throw.
        facade.login("player3", "password");
        // Attempt to create a game with an invalid (empty) name.
        ResponseException ex = assertThrows(ResponseException.class, () -> {
            facade.createGame("");
        });
        assertTrue(ex.getMessage().toLowerCase().contains("expected") ||
                        ex.getMessage().toLowerCase().contains("error"),
                "Error should indicate that a valid game name is expected.");
    }

    // ======= LIST GAMES =======

    @Test
    public void testListGamesPositive() throws Exception {
        UserData user = new UserData("player3", "password", "player3@example.com");
        facade.register(user);
        // The login call should succeed and not throw.
        facade.login("player3", "password");
        // Create a game to ensure there is at least one game in the list.
        facade.createGame("ListTestGame");
        GameListResponse listResponse = facade.listGames();
        assertNotNull(listResponse, "List response should not be null.");
        assertTrue(listResponse.games().size() > 0, "There should be at least one game listed.");
    }

    @Test
    public void testListGamesNegative() throws Exception {
        UserData user = new UserData("player3", "password", "player3@example.com");
        facade.register(user);
        // The login call should succeed and not throw.
        facade.login("player3", "password");
        GameListResponse listResponse = facade.listGames();
        // Expect an empty list.
        assertTrue(listResponse.games().isEmpty(), "No games should be listed when the database is empty.");
    }
}


