package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.JoinObserver;
import websocket.commands.JoinPlayer;
import websocket.commands.Leave;
import websocket.commands.MakeMove;
import websocket.commands.Resign;
import websocket.messages.Notification;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

// The WebsocketFacade extends Endpoint so that it can be registered with the container.
public class WebsocketFacade extends Endpoint {

    private Session session;
    private final NotificationHandler notificationHandler;
    private final Gson gson = new Gson();
    String authtoken;

    // The facade constructor establishes the WebSocket connection.
    public WebsocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            // Convert the HTTP URL to a WebSocket URL and add the /ws endpoint.
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // Set a message handler that deserializes incoming messages into a Notification and passes it to our handler.
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = gson.fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // onOpen is required by the Endpoint API; you can add logic here if needed.
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connection opened.");
    }

    // === Public API Methods ===

    // Joins a game as a player. The caller must supply the auth token, game ID, and desired color.
    public void joinAsPlayer(int gameID, String color) throws ResponseException {
        try {
            JoinPlayer command = new JoinPlayer(authtoken, gameID, color);
            String json = gson.toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // Joins a game as an observer.
    public void joinAsObserver(int gameID) throws ResponseException {
        try {
            JoinObserver command = new JoinObserver(authtoken, gameID);
            String json = gson.toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // Sends a move command. The ChessMove object should represent your move data.
    public void makeMove(int gameID, ChessMove move) throws ResponseException {
        try {
            MakeMove command = new MakeMove(authtoken, gameID, move);
            String json = gson.toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // Leaves a game.
    public void leaveGame(int gameID) throws ResponseException {
        try {
            Leave command = new Leave(authtoken, gameID);
            String json = gson.toJson(command);
            this.session.getBasicRemote().sendText(json);
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // Resigns from a game.
    public void resignGame(int gameID) throws ResponseException {
        try {
            Resign command = new Resign(authtoken, gameID);
            String json = gson.toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
