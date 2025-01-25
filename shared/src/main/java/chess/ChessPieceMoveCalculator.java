package chess;

import java.util.Collection;

public interface ChessPieceMoveCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}
