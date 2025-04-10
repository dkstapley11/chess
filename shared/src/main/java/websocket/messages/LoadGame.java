package websocket.messages;

import chess.ChessBoard;

public class LoadGame extends ServerMessage {

    private final ChessBoard game; // Replace Object with your actual game type if needed

    public LoadGame(ChessBoard game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessBoard getGame() {
        return game;
    }
}
