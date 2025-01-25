package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements ChessPieceMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        //iterate upwards and add to the moves array until we go out of bounds or hit a piece
        int upRow = row + 1;
        while (true)  {
            ChessPosition target = new ChessPosition(upRow, col);
            if (!isInBounds(target)) {
                break;
            }
            if (isSquareEmpty(board, target)) {
                moves.add(new ChessMove(position, target, null));
                upRow++;
            } else {
                ChessGame.TeamColor targetColor = board.getPiece(target).getTeamColor();
                if (color != targetColor) {
                    moves.add(new ChessMove(position, target, null));
                }
                break;
            }
        }
        // iterate downwards
        int downRow = row - 1;
        while (true)  {
            ChessPosition target = new ChessPosition(downRow, col);
            if (!isInBounds(target)) {
                break;
            }
            if (isSquareEmpty(board, target)) {
                moves.add(new ChessMove(position, target, null));
                downRow--;
            } else {
                ChessGame.TeamColor targetColor = board.getPiece(target).getTeamColor();
                if (color != targetColor) {
                    moves.add(new ChessMove(position, target, null));
                }
                break;
            }
        }
        // iterate left
        int leftCol = col - 1;
        while (true)  {
            ChessPosition target = new ChessPosition(row, leftCol);
            if (!isInBounds(target)) {
                break;
            }
            if (isSquareEmpty(board, target)) {
                moves.add(new ChessMove(position, target, null));
                leftCol--;
            } else {
                ChessGame.TeamColor targetColor = board.getPiece(target).getTeamColor();
                if (color != targetColor) {
                    moves.add(new ChessMove(position, target, null));
                }
                break;
            }
        }
        // iterate right
        int rightCol = col + 1;
        while (true)  {
            ChessPosition target = new ChessPosition(row, rightCol);
            if (!isInBounds(target)) {
                break;
            }
            if (isSquareEmpty(board, target)) {
                moves.add(new ChessMove(position, target, null));
                rightCol++;
            } else {
                ChessGame.TeamColor targetColor = board.getPiece(target).getTeamColor();
                if (color != targetColor) {
                    moves.add(new ChessMove(position, target, null));
                }
                break;
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
}
