package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements ChessPieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[] validOffsets = {-1, 0, 1};
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int newRow = currentRow + validOffsets[i];
                int newCol = currentCol + validOffsets[j];
                if (isLegalMove(board, new ChessPosition(newRow, newCol), position)) {
                    moves.add(new ChessMove(position, new ChessPosition(newRow, newCol), null));
                }
            }
        }
        return moves;
    }
    private boolean isLegalMove(ChessBoard board, ChessPosition target, ChessPosition kingPosition) {
        int row = target.getRow();
        int col = target.getColumn();
        // didn't move
        if (target.equals(kingPosition)) {
            return false;
        }
        // check if out of bounds
        if (row > 8 || col > 8 || row < 1 || col < 1) {
            return false;
        }
        // make sure other piece is not the same team
        ChessPiece targetPiece = board.getPiece(target);
        return targetPiece == null || targetPiece.getTeamColor() != board.getPiece(kingPosition).getTeamColor();
    }
}
