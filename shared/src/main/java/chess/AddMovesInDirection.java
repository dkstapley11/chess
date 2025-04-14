package chess;

import java.util.Collection;

public class AddMovesInDirection {

    public static void addMovesInDirection(
            Collection<ChessMove> moves,
            ChessBoard board,
            ChessGame.TeamColor color,
            ChessPosition position,
            int rowOffset, int colOffset
    ) {
        int row = position.getRow();
        int col = position.getColumn();
        while (true) {
            row += rowOffset;
            col += colOffset;
            ChessPosition target = new ChessPosition(row, col);
            if (target.outOfBounds(row, col)) {
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

    public static boolean squareEmpty(ChessBoard board, ChessPosition position) {
        return board.getPiece(position) == null;
    }
}
