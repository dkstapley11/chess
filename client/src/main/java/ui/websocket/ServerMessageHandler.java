package ui.websocket;

import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage message, String stringMessage);
}