package websocket.messages;

import chess.ChessGame;

public class LoadGame extends ServerMessage {

    private final ChessGame game; // Replace Object with your actual game type if needed

    public LoadGame(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Object getGame() {
        return game;
    }
}
