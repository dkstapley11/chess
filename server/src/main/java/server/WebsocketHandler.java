package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebsocketHandler {

    private static final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New WebSocket connection established: " + session.getRemoteAddress().getAddress());
        // Initially, the session is not associated with any game (0 means "none")
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
            // First, deserialize to the base class to determine the command type.
            UserGameCommand baseCommand = gson.fromJson(message, UserGameCommand.class);
            switch (baseCommand.getCommandType()) {
                case JOIN_PLAYER:
                    JoinPlayer joinPlayer = gson.fromJson(message, JoinPlayer.class);
                    handleJoinPlayerCommand(session, joinPlayer);
                    break;
                case JOIN_OBSERVER:
                    JoinObserver joinObserver = gson.fromJson(message, JoinObserver.class);
                    handleJoinObserverCommand(session, joinObserver);
                    break;
                case MAKE_MOVE:
                    MakeMove makeMove = gson.fromJson(message, MakeMove.class);
                    handleMakeMoveCommand(session, makeMove);
                    break;
                case LEAVE:
                    Leave leave = gson.fromJson(message, Leave.class);
                    handleLeaveCommand(session, leave);
                    break;
                case RESIGN:
                    Resign resign = gson.fromJson(message, Resign.class);
                    handleResignCommand(session, resign);
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

    private void handleJoinPlayerCommand(Session session, JoinPlayer command) throws Exception {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            ChessGame.TeamColor joiningColor =
                    command.getColor().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            // Check that the player's username matches the expected color assignment.
            boolean correctColor;
            if (joiningColor == ChessGame.TeamColor.WHITE) {
                correctColor = Objects.equals(game.whiteUsername(), auth.username());
            } else {
                correctColor = Objects.equals(game.blackUsername(), auth.username());
            }

            if (!correctColor) {
                Error error = new Error("Error: attempting to join with wrong color");
                sendMessage(session, error);
                return;
            }

            Server.gameSessions.put(session, command.getGameID());

            Notification notif = new Notification(
                    String.format("%s has joined the game as %s", auth.username(), command.getColor()));
            broadcastMessage(session, notif);

            LoadGame load = new LoadGame(game.game());
            sendMessage(session, load);
        } catch (ResponseException e) {
            sendMessage(session, new Error(e.getMessage()));
        }
    }

    private void handleJoinObserverCommand(Session session, JoinObserver command) throws Exception {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            Server.gameSessions.put(session, command.getGameID());

            Notification notif = new Notification(
                    String.format("%s has joined the game as an observer", auth.username()));
            broadcastMessage(session, notif);

            LoadGame load = new LoadGame(game.game());
            sendMessage(session, load);
        } catch (Exception e) {
            sendMessage(session, new Error("Error: Not authorized"));
        }
    }

    // Handles a MAKE_MOVE command.
    private void handleMakeMoveCommand(Session session, MakeMove command) throws Exception {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);
            if (userColor == null) {
                sendMessage(session, new Error("Error: You are observing this game"));
                return;
            }
            if (game.game().isGameOver()) {
                sendMessage(session, new Error("Error: cannot make a move, game is over"));
                return;
            }
            if (!game.game().getTeamTurn().equals(userColor)) {
                sendMessage(session, new Error("Error: it is not your turn"));
                return;
            }

            // Process the move (using your chess logic).
            game.game().makeMove(command.getMove());

            Notification notif;
            ChessGame.TeamColor opponentColor =
                    userColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            if (game.game().isInCheckmate(opponentColor)) {
                notif = new Notification(String.format("Checkmate! %s wins!", auth.username()));
                game.game().setGameOver(true);
            } else if (game.game().isInStalemate(opponentColor)) {
                notif = new Notification(String.format("Stalemate caused by %s's move! It's a tie!", auth.username()));
                game.game().setGameOver(true);
            } else if (game.game().isInCheck(opponentColor)) {
                notif = new Notification(String.format("A move has been made by %s, %s is now in check!",
                        auth.username(), opponentColor));
            } else {
                notif = new Notification(String.format("A move has been made by %s", auth.username()));
            }
            // Broadcast the notification to all clients in this game.
            broadcastMessage(session, notif, true);

            // Update the game in the database.
            Server.gameService.updateGame(auth.authToken(), game);

            LoadGame load = new LoadGame(game.game());
            broadcastMessage(session, load, true);
        } catch (ResponseException e) {
            sendMessage(session, new Error(e.getMessage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles a LEAVE command.
    private void handleLeaveCommand(Session session, Leave command) throws ResponseException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            Notification notif = new Notification(String.format("%s has left the game", auth.username()));
            broadcastMessage(session, notif);
            session.close();
        } catch (ResponseException e) {
            sendMessage(session, new Error(e.getMessage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles a RESIGN command.
    private void handleResignCommand(Session session, Resign command) throws Exception {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);
            if (userColor == null) {
                sendMessage(session, new Error("Error: You are observing this game"));
                return;
            }
            if (game.game().isGameOver()) {
                sendMessage(session, new Error("Error: The game is already over!"));
                return;
            }
            game.game().setGameOver(true);
            Server.gameService.updateGame(auth.authToken(), game);
            String opponentUsername = userColor == ChessGame.TeamColor.WHITE ?
                    game.blackUsername() : game.whiteUsername();
            Notification notif = new Notification(
                    String.format("%s has forfeited, %s wins!", auth.username(), opponentUsername));
            broadcastMessage(session, notif, true);
        } catch (ResponseException e) {
            sendMessage(session, new Error(e.getMessage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================
    // Utility Methods
    // =====================

    // Sends a ServerMessage to a single session.
    private void sendMessage(Session session, ServerMessage message) {
        try {
            if (session.isOpen()) {
                session.getRemote().sendString(gson.toJson(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcasts a message to all sessions in the same game as currSession.
    public void broadcastMessage(Session currSession, ServerMessage message) throws IOException {
        broadcastMessage(currSession, message, false);
    }

    public void broadcastMessage(Session currSession, ServerMessage message, boolean toSelf) throws IOException {
        System.out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, gson.toJson(message));
        for (Session session : Server.gameSessions.keySet()) {
            boolean inAGame = !Objects.equals(Server.gameSessions.get(session), 0);
            boolean sameGame = Server.gameSessions.get(session).equals(Server.gameSessions.get(currSession));
            boolean isSelf = session == currSession;
            if ((toSelf || !isSelf) && inAGame && sameGame) {
                sendMessage(session, message);
            }
        }
    }

    // Determines the team color of a user in a game.
    private ChessGame.TeamColor getTeamColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

    // Sends an error message (ensuring the message includes "error").
    private void sendErrorMessage(Session session, String errorMessage) throws IOException {
        System.out.printf("Error: %s%n", errorMessage);
        sendMessage(session, new Error("Error: " + errorMessage));
    }
}
