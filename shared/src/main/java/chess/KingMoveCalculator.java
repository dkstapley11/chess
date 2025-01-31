package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

        int row = position.getRow();
        int col = position.getColumn();

        int [][] potentialMoves = {{1,1}, {1,0}, {1,-1}, {0,1}, {0,-1}, {-1,1}, {-1,0}, {-1,-1}};

        for (int[] move : potentialMoves) {
            if (validateMove(board, position, move, color)) {
                moves.add(new ChessMove(position, new ChessPosition(row + move[0], col + move[1]), null));
            }
        }

        return moves;
    }

    public boolean validateMove(ChessBoard board, ChessPosition position, int[] move, ChessGame.TeamColor color) {
        int targetRow = position.getRow() + move[0];
        int targetCol = position.getColumn() + move[1];
        if (outOfBounds(targetRow, targetCol)) {
            return false;
        }
        ChessPosition target = new ChessPosition(targetRow, targetCol);
        if (squareEmpty(board, target)) {
            return true;
        }
        // if enemy
        if (!squareEmpty(board, target) && board.getPiece(target).getTeamColor() != color) {
            return true;
        }
        // can only mean friendly piece
        return false;
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
