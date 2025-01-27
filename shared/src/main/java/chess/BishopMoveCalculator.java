package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator implements ChessPieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

        // iterate for every direction
        addDiagonalMoves(moves, board, position, color, 1, 1);
        addDiagonalMoves(moves, board, position, color, -1, 1);
        addDiagonalMoves(moves, board, position, color, 1, -1);
        addDiagonalMoves(moves, board, position, color, -1, -1);

        return moves;
    }

    private void addDiagonalMoves (Collection<ChessMove> moves, ChessBoard board, ChessPosition position, ChessGame.TeamColor color, int rowDirection, int colDirection) {
        int startRow = position.getRow();
        int startCol = position.getColumn();
        System.out.println(board.toString());
        while (true)  {
            startRow += rowDirection;
            startCol += colDirection;
            ChessPosition target = new ChessPosition(startRow, startCol);
            System.out.println("Exploring target: " + target.getRow() + ", " + target.getColumn()); // Debug logging
            if (!isInBounds(target)) {
                System.out.println("first break reached");
                break;
            }
            if (isSquareEmpty(board, target)) {
                moves.add(new ChessMove(position, target, null));
            } else {
                ChessGame.TeamColor targetColor = board.getPiece(target).getTeamColor();
                if (color != targetColor) {
                    moves.add(new ChessMove(position, target, null));
                }
                System.out.println("second break reached");
                break;
            }
        }

    }

    private boolean isSquareEmpty(ChessBoard board, ChessPosition square) {
        if (board.getPiece(square) != null) System.out.println("this square contains: " + board.getPiece(square).getPieceType());
        return board.getPiece(square) == null;
    }

    private boolean isInBounds(ChessPosition target) {
        int row = target.getRow();
        int col = target.getColumn();


        return row <= 8 && col <= 8 && row >= 1 && col >= 1;
    }
}
