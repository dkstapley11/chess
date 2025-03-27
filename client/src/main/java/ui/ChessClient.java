package ui;

import java.util.ArrayList;
import java.util.Arrays;

import model.UserData;
import exception.ResponseException;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    // Holds the game IDs in the order they were listed.
    private ArrayList<Integer> lastGameIds = new ArrayList<>();

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) throws ResponseException {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "list" -> listGames();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "help" -> help();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            throw ex;
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            visitorName = params[0];
            server.login(params[0], params[1]);
            state = State.SIGNEDIN;
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);
            server.register(user);
            state = State.SIGNEDIN;
            return String.format("You registered as %s.", username);
        }
        throw new ResponseException(400, "Unable to register");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        var games = server.listGames().games();
        StringBuilder result = new StringBuilder();
        // Clear previous mapping.
        lastGameIds.clear();

        if (games == null || games.isEmpty()) {
            return "No games available.";
        }

        int index = 1;
        for (var game : games) {
            // Save the actual game ID.
            lastGameIds.add(game.gameID());
            result.append(index).append(". ")
                    .append("Game Name: ").append(game.gameName());

            if (game.game() != null && game.game().getTeamTurn() != null) {
                result.append(" - Turn: ").append(game.game().getTeamTurn());
            }

            result.append("\n   Players: ");
            result.append("\nWhite: ").append(game.whiteUsername() != null ? game.whiteUsername() : "None");
            result.append("\nBlack: ").append(game.blackUsername() != null ? game.blackUsername() : "None");
            result.append("\n");
            index++;
        }
        return result.toString();
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (lastGameIds.isEmpty()) {
            return "No game list available. Please run the list command first.";
        }
        if (params.length != 2) {
            throw new ResponseException(400, "Expected: join <game number> <WHITE|BLACK>");
        }
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Invalid game number format.");
        }
        if (gameNumber < 1 || gameNumber > lastGameIds.size()) {
            throw new ResponseException(400, "Game number out of range. Please list games and try again.");
        }
        int id = lastGameIds.get(gameNumber - 1);
        // For joinGame, we use a default color, e.g., WHITE.
        server.joinGame(params[1], id);
        boolean whitePerspective;
        String color = params[1];
        if (color.equals("white")) {
            whitePerspective = true;
        }
        else if (color.equals("black")) {
            whitePerspective = false;
        }
        else {
            return "Not a valid color";
        }
        ChessBoardPrinter.printStartBoard(whitePerspective);
        return "You joined game number " + gameNumber +  "as the" + color + "player";
    }

    /**
     * Observes a game (joins as a spectator).
     * Expects a single parameter: the game number.
     */
    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (lastGameIds.isEmpty()) {
            return "No game list available. Please run the list command first.";
        }
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: observe <game number>");
        }
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Invalid game number format.");
        }
        if (gameNumber < 1 || gameNumber > lastGameIds.size()) {
            throw new ResponseException(400, "Game number out of range. Please list games and try again.");
        }
        int id = lastGameIds.get(gameNumber - 1);
        // Join game as spectator (pass null for color).
//        server.joinGame(null, id);
        // Spectators view from white perspective.
        ChessBoardPrinter.printStartBoard(true);
        return "You are now observing game number " + gameNumber;
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            var gameName = params[0];
            server.createGame(gameName);
            return "You created game with name: " + gameName;
        }
        throw new ResponseException(400, "Expected: <game name>");
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        state = State.SIGNEDOUT;
        server.logout();
        return String.format("%s left the shop", visitorName);
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - login <username> <password>
                    - register <username> <password> <email>
                    - quit - playing chess
                    """;
        }
        return """
                - create <NAME> - a game
                - list - games
                - join <game number> - join a game as a player (default: WHITE)
                - observe <game number> - observe a game as a spectator
                - logout - when you are done
                - help - with possible commands
                - quit - playing chess
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
