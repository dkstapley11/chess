package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import Exception.ResponseException;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

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
                case "observe" -> joinGame(params);
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
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
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

        if (games == null || games.isEmpty()) {
            return "No games available.";
        }

        int index = 1;
        for (var game : games) {
            result.append(index).append(". ")
                    .append("Game Name: ").append(game.gameName())
                    .append(" (ID: ").append(game.gameID()).append(")");

            if (game.game() != null && game.game().getTeamTurn() != null) {
                result.append(" - Turn: ").append(game.game().getTeamTurn());
            }

            if (game.blackUsername() != null && game.whiteUsername() != null) {
                result.append("\n   Players: ");
                result.append("\nBlack: ").append(game.blackUsername());
                result.append("\nWhite: ").append(game.whiteUsername());
            }

            result.append("\n"); // Newline after each game
            index++;
        }
        return result.toString();
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        boolean whitePerspective = true;
        if (params.length == 1) {
            int id = Integer.parseInt(params[0]);
            server.joinGame(null, id);
            // Spectators view from white perspective.
            whitePerspective = true;
            ChessBoard.drawBoard(whitePerspective);
            return "You joined game with id: " + id + " as a spectator";
        }
        if (params.length == 2) {
            String color = params[0].toUpperCase();
            int id = Integer.parseInt(params[1]);
            server.joinGame(color, id);
            // Use white perspective if playing white; otherwise, black perspective.
            whitePerspective = !color.equals("BLACK");
            ChessBoard.drawBoard(whitePerspective);
            return "You joined game with id: " + id + " as the " + color + " player";
        }
        throw new ResponseException(400, "Expected: join <game id> [<color>]");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            var gameName = params[0];
            server.createGame(gameName);
            return ("You created game with name: " + gameName);
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
                - join <ID> <WHITE|BLACK> - a game
                - observe <ID> - a game
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
