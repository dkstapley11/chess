package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.AddMovesInDirection.addMovesInDirection;

public class BishopMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

        addMovesInDirection(moves, board, color, position, 1, 1);
        addMovesInDirection(moves, board, color, position, 1, -1);
        addMovesInDirection(moves, board, color, position, -1, 1);
        addMovesInDirection(moves, board, color, position, -1, -1);

        return moves;
    }
}
