package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebsocketHandler {

    private static final Gson gson = new Gson();


    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New WebSocket connection established: " + session.getRemoteAddress().getAddress());
        Server.gameSessions.put(session, 0);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + session.getRemoteAddress().getAddress() +
                " Code: " + statusCode + " Reason: " + reason);
        Server.gameSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("Received message: " + message);

        try {
            // Deserialize incoming JSON to a UserGameCommand object
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            // Process the command based on its type
            switch (command.getCommandType()) {
                case CONNECT:
                    handleConnectCommand(session, command);
                    break;
                case MAKE_MOVE:
                    handleMakeMoveCommand(session, command);
                    break;
                case LEAVE:
                    handleLeaveCommand(session, command);
                    break;
                case RESIGN:
                    handleResignCommand(session, command);
                    break;
                default:
                    sendErrorMessage(session, "Unknown command type.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorMessage(session, "Error processing message: " + e.getMessage());
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.out.println("WebSocket error: " + error.getMessage());
        error.printStackTrace();
    }

    // === Command Handling Methods ===

    // Handles the CONNECT command:
    // - Validates the user (authToken, gameID, etc.)
    // - Sends a LOAD_GAME message to the connecting (root) client
    // - Broadcasts a NOTIFICATION to other connected clients in the game
    private void handleConnectCommand(Session session, UserGameCommand command) throws Exception {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            ChessGame.TeamColor joiningColor = command.getTeamColor().toString().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            boolean correctColor;
            if (joiningColor == ChessGame.TeamColor.WHITE) {
                correctColor = Objects.equals(game.whiteUsername(), auth.username());
            }
            else {
                correctColor = Objects.equals(game.blackUsername(), auth.username());
            }

            if (!correctColor) {
                Error error = new Error("Error: attempting to join with wrong color");
                sendError(session, error);
                return;
            }

            Notification notif = new Notification("%s has joined the game as %s".formatted(auth.username(), command.getColor().toString()));
            broadcastMessage(session, notif);

            LoadGame load = new LoadGame(game.game());
            sendMessage(session, load);
        }
        catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: Not a valid game"));
        }

    }

    // Handles the MAKE_MOVE command:
    // - Validates and processes the move
    // - Updates the game state and the database
    // - Sends a LOAD_GAME update to all clients and broadcasts a move notification
    private void handleMakeMoveCommand(Session session, UserGameCommand command) {
        // TODO: Process the move using your chess logic (e.g., gameService.makeMove(...))
        // For demonstration, create a dummy updated game state:
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGameMessage.setGame("Updated game state after move"); // Replace with actual updated game

        // Send updated game state to the current session
        sendMessage(session, loadGameMessage);

        // TODO: Broadcast a NOTIFICATION message to all other sessions:
        // e.g., "Alice made move: E2-E4"
    }

    // Handles the LEAVE command:
    // - Processes the removal of the user from the game
    // - Updates the database if necessary
    // - Sends a NOTIFICATION to other clients in the game
    private void handleLeaveCommand(Session session, UserGameCommand command) {
        // TODO: Remove the user from the game and update your DB

        ServerMessage notification = new ServerMessage();
        notification.setServerMessageType(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage("Player has left the game.");

        sendMessage(session, gson.toJson(notification));

        // TODO: Broadcast this notification to other connected sessions in the game
    }

    // Handles the RESIGN command:
    // - Marks the game as over, updates the database, and sends a resignation notification
    private void handleResignCommand(Session session, UserGameCommand command) {
        // TODO: Process the resignation logic, update game state and DB

        ServerMessage notification = new ServerMessage();
        notification.setServerMessageType(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage("Player has resigned.");

        sendMessage(session, gson.toJson(notification));

        // TODO: Broadcast this notification to all sessions in the game
    }

    // === Utility Methods ===

    // Sends a message to a single session
    private void sendMessage(Session session, ServerMessage message) {
        try {
            if (session.isOpen()) {
                session.getRemote().sendString(gson.toJson(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(Session currSession, ServerMessage message) throws IOException {
        broadcastMessage(currSession, message, false);
    }

    public void broadcastMessage(Session currSession, ServerMessage message, boolean toSelf) throws IOException {
        System.out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, gson.toJson(message));
        // Loop through all sessions tracked in the Server.gameSessions map.
        for (Session session : Server.gameSessions.keySet()) {
            // Check that the session is in a game and that it is in the same game as the originating session.
            boolean inAGame = !Objects.equals(Server.gameSessions.get(session), 0);
            boolean sameGame = Server.gameSessions.get(session).equals(Server.gameSessions.get(currSession));
            boolean isSelf = session == currSession;
            if ((toSelf || !isSelf) && inAGame && sameGame) {
                sendMessage(session, message);
            }
        }
    }

    private ChessGame.TeamColor getTeamColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

    // Sends an error message to the client, ensuring the message includes the word "error"
    private void sendErrorMessage(Session session, String errorMessage) throws IOException {
        System.out.printf("Error: %s%n", errorMessage);
        session.getRemote().sendString(errorMessage);
    }
}
