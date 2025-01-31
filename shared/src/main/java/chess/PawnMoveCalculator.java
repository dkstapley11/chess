package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        if (color == ChessGame.TeamColor.BLACK) {
            addPawnMoves(board, moves, position, color, -1);
        } else {
            addPawnMoves(board, moves, position, color, 1);
        }
        return moves;
    }

    public void addPawnMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition position, ChessGame.TeamColor color, int direction) {

        int row = position.getRow();
        int col = position.getColumn();
        // define possible moves
        ChessPosition forward = new ChessPosition(row + direction, col);
        ChessPosition diagLeft = new ChessPosition(row + direction, col -1);
        ChessPosition diagRight = new ChessPosition(row + direction, col + 1);
        // check if it's the first move, if so, no worry about out of bounds
        if (isFirstMove(board, position) && squareEmpty(board, new ChessPosition(row + direction, col)) && squareEmpty(board, new ChessPosition(row + direction + direction, col))) {
            moves.add(new ChessMove(position, new ChessPosition(row + direction + direction, col), null));
        }
        // check move one space forward, non promotion move
        if (squareEmpty(board, forward) && row + direction != 8 && row + direction != 1) {
            moves.add(new ChessMove(position, forward, null));
        }
        // handle forward promotion move
        if (squareEmpty(board, forward) && (row + direction == 8 || row + direction == 1)) {
            addPromotions(moves, position, forward);
        }
        // diagonal captures
        // if not out of bounds, not empty, is an enemy piece, and not a promotion...
        if (!outOfBounds(diagRight.getRow(), diagRight.getColumn()) && !squareEmpty(board, diagRight) && board.getPiece(diagRight).getTeamColor() != color && row + direction != 8 && row + direction != 1) {
            moves.add(new ChessMove(position, diagRight, null));
        }
        // same for diagLeft
        if (!outOfBounds(diagLeft.getRow(), diagLeft.getColumn()) && !squareEmpty(board, diagLeft) && board.getPiece(diagLeft).getTeamColor() != color && row + direction != 8 && row + direction != 1) {
            moves.add(new ChessMove(position, diagLeft, null));
        }
        // now diagonal promotion moves. if not out of bounds, not empty, enemy piece, and on 8th or 1st rank...
        if (!outOfBounds(diagRight.getRow(), diagRight.getColumn()) && !squareEmpty(board, diagRight) && board.getPiece(diagRight).getTeamColor() != color && (row + direction == 8 || row + direction == 1)) {
            addPromotions(moves, position, diagRight);
        }
        if (!outOfBounds(diagLeft.getRow(), diagLeft.getColumn()) && !squareEmpty(board, diagLeft) && board.getPiece(diagLeft).getTeamColor() != color && (row + direction == 8 || row + direction == 1)) {
            addPromotions(moves, position, diagLeft);
        }
    }

    public boolean isFirstMove(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        if (color == ChessGame.TeamColor.BLACK && row == 7) {
            return true;
        }
        if (color == ChessGame.TeamColor.WHITE && row == 2) {
            return true;
        }
        return false;
    }

    public void addPromotions(Collection<ChessMove> moves, ChessPosition position, ChessPosition target) {
        moves.add(new ChessMove(position, target, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(position, target, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(position, target, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(position, target, ChessPiece.PieceType.BISHOP));
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
