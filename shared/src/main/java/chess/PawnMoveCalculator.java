package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements ChessPieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        int row = position.getRow();
        int col = position.getColumn();

        ChessPosition bForward = new ChessPosition(row - 1, col);
        ChessPosition bDownLeft = new ChessPosition(row - 1, col - 1);
        ChessPosition bDownRight = new ChessPosition(row - 1, col + 1);

        if (color == ChessGame.TeamColor.BLACK) {
            if (isFirstMove(position, color) && isSquareEmpty(board, bForward) && isSquareEmpty(board, new ChessPosition(row - 2, col))) {
                moves.add(new ChessMove(position, new ChessPosition(row-2, col), null));
            }
            if (isInBounds(bForward) && isSquareEmpty(board, bForward) && bForward.getRow() != 1) {
                moves.add(new ChessMove(position, bForward, null));
            }
            if (isInBounds(bForward) && isSquareEmpty(board, bForward) && bForward.getRow() == 1) {
                addPromotions(moves, position, bForward);
            }
            if (isInBounds(bDownLeft) && board.getPiece(bDownLeft).getTeamColor() == ChessGame.TeamColor.WHITE && bDownLeft.getRow() != 1) {
                moves.add(new ChessMove(position, bDownLeft, null));
            }
            if (isInBounds(bDownLeft) && board.getPiece(bDownLeft).getTeamColor() == ChessGame.TeamColor.WHITE && bDownLeft.getRow() == 1) {
                addPromotions(moves, position, bDownLeft);
            }
            if (isInBounds(bDownRight) && board.getPiece(bDownRight).getTeamColor() == ChessGame.TeamColor.WHITE && bDownRight.getRow() != 1) {
                moves.add(new ChessMove(position, bDownRight, null));
            }
            if (isInBounds(bDownRight) && board.getPiece(bDownRight).getTeamColor() == ChessGame.TeamColor.WHITE && bDownRight.getRow() == 1) {
                addPromotions(moves, position, bDownRight);
            }
        }

        ChessPosition wForward = new ChessPosition(row + 1, col);
        ChessPosition wUpRight = new ChessPosition(row + 1, col + 1);
        ChessPosition wUpLeft = new ChessPosition(row + 1, col - 1);
        if (color == ChessGame.TeamColor.WHITE) {
            if (isFirstMove(position, color) && isSquareEmpty(board, wForward) && isSquareEmpty(board, new ChessPosition(row + 2, col))) {
                moves.add(new ChessMove(position, new ChessPosition(row+2, col), null));
            }
            if (isInBounds(wForward) && isSquareEmpty(board, wForward) && wForward.getRow() != 8) {
                moves.add(new ChessMove(position, wForward, null));
            }
            if (isInBounds(wForward) && isSquareEmpty(board, wForward) && wForward.getRow() == 8) {
                addPromotions(moves, position, wForward);
            }
            if (isInBounds(wUpLeft) && board.getPiece(wUpLeft).getTeamColor() == ChessGame.TeamColor.BLACK && wUpLeft.getRow() != 8) {
                moves.add(new ChessMove(position, wUpLeft, null));
            }
            if (isInBounds(wUpLeft) && board.getPiece(wUpLeft).getTeamColor() == ChessGame.TeamColor.BLACK && wUpLeft.getRow() == 8) {
                addPromotions(moves, position, wUpLeft);
            }
            if (isInBounds(wUpRight) && board.getPiece(wUpRight).getTeamColor() == ChessGame.TeamColor.BLACK && wUpRight.getRow() != 8) {
                moves.add(new ChessMove(position, wUpRight, null));
            }
            if (isInBounds(wUpRight) && board.getPiece(wUpRight).getTeamColor() == ChessGame.TeamColor.BLACK && wUpRight.getRow() == 8) {
                addPromotions(moves, position, wUpRight);
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

    private boolean isFirstMove(ChessPosition position, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE && position.getRow() == 2) {
            return true;
        }
        if (color == ChessGame.TeamColor.BLACK && position.getRow() == 7) {
            return true;
        }
        return false;
    }

    private void addPromotions(Collection<ChessMove> moves, ChessPosition position, ChessPosition endPosition) {
        moves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.BISHOP));
    }
}
