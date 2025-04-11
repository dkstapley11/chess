package ui.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.commands.Connect;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {

    private Session session;
    private final ServerMessageHandler notificationHandler;
    private final Gson gson = new Gson();
    String authtoken;

    public WebsocketFacade(String url, ServerMessageHandler notificationHandler, String authtoken) throws ResponseException {
        try {
            // Convert the HTTP URL to a WebSocket URL and add the /ws endpoint.
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;
            this.authtoken = authtoken;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    notificationHandler.notify(serverMessage, message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connection opened.");
    }


    public void joinAsPlayer(int gameID) throws ResponseException {
        try {
            Connect command = new Connect(authtoken, gameID);
            String json = gson.toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // Joins a game as an observer.
    public void joinAsObserver(int gameID) throws ResponseException {
        try {
            Connect command = new Connect(authtoken, gameID);
            String json = gson.toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    // Sends a move command. The ChessMove object should represent your move data.
    public void makeMove(int gameID, ChessMove move, boolean isWhite) throws ResponseException {
        try {
            MakeMove command = new MakeMove(authtoken, gameID, move, isWhite);
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
