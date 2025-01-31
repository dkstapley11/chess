package chess;

import java.util.ArrayList;
import java.util.Collection;

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

    public void addMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessGame.TeamColor color, ChessPosition position, int rowOffset, int colOffset) {
        int row = position.getRow();
        int col = position.getColumn();
        while (true) {
            row += rowOffset;
            col += colOffset;
            ChessPosition target = new ChessPosition(row, col);
            if (outOfBounds(row, col)) {
                break;
            }
            if (squareEmpty(board, target)) {
                moves.add(new ChessMove(position, target, null));
            } else { // either friendly or enemy piece. if enemy, add move and break. if friend, just break
                if (board.getPiece(target).getTeamColor() != color) {
                    moves.add(new ChessMove(position, target, null));
                }
                break;
            }
        }
    }

    public boolean outOfBounds(int row, int col) {
        if (row > 8) return true;
        if (col > 8) return true;
        if (row < 1) return true;
        if (col < 1) return true;
        return false;
    }

    public boolean squareEmpty(ChessBoard board, ChessPosition position) {
        return board.getPiece(position) == null;
    }
}
