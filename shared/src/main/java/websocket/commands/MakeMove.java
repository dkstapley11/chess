package websocket.commands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {

    private final ChessMove move;
    private final boolean isWhite;

    public MakeMove(String authToken, Integer gameID, ChessMove move, boolean isWhite) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.isWhite = isWhite;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public ChessMove getMove() {
        return move;
    }
}
