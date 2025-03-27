package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements ChessPieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

        int[][] possibleMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : possibleMoves) {
            int newRow = position.getRow() + move[0];
            int newCol = position.getColumn() + move[1];
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            if (isValidMove(board, newPos, color)) {
                moves.add(new ChessMove(position, newPos, null));
            }

        }

        return moves;
    }

    private boolean isSquareEmpty(ChessBoard board, ChessPosition square) {
        return board.getPiece(square) == null;
    }

    private boolean isInBounds(ChessPosition target) {
        int row = target.getRow();
        int col = target.getColumn();

        return row <= 8 && col <= 8 && row >= 1 && col >= 1;
    }

    private boolean isValidMove(ChessBoard board, ChessPosition target, ChessGame.TeamColor color) {
        if (!isInBounds(target)) {
            return false;
        }
        if (isSquareEmpty(board, target)) {
            return true;
        }
        ChessGame.TeamColor targetColor = board.getPiece(target).getTeamColor();
        return targetColor != color;
    }
}
