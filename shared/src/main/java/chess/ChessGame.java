package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor color;
    private TeamColor turn;
    private ChessBoard board;
    private ChessPosition WKingPosition;
    private ChessPosition BKingPosition;


    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.WKingPosition = new ChessPosition(1, 5);
        this.BKingPosition = new ChessPosition(8, 5);
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        Collection<ChessMove> Moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        for (ChessMove move : Moves) {

        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    public ChessPosition findKing(ChessBoard board, TeamColor color) {
        for (int i = 0; i < 8; i++) {
            for (int k = 0; k < 8; k++) {
                ChessPosition currentPos = new ChessPosition(i, k);
                // if statement here to make sure it's not empty??
                ChessPiece currentPiece = board.getPiece(currentPos);
                if (currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == color) {
                    return currentPos;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition;
        if (teamColor == TeamColor.WHITE) {
            kingPosition = WKingPosition;
        } else {
            kingPosition = BKingPosition;
        }

        // Check diagonal danger
        if (checkDiagonalDanger(kingPosition, teamColor, 1, 1) ||
                checkDiagonalDanger(kingPosition, teamColor, 1, -1) ||
                checkDiagonalDanger(kingPosition, teamColor, -1, 1) ||
                checkDiagonalDanger(kingPosition, teamColor, -1, -1)) {
            return true;
        }

        // Check knight danger
        if (checkKnightDanger(kingPosition, teamColor)) {
            return true;
        }

        // Check straight danger
        if (checkStraightDanger(kingPosition, teamColor, 1, 0) ||
                checkStraightDanger(kingPosition, teamColor, -1, 0) ||
                checkStraightDanger(kingPosition, teamColor, 0, -1) ||
                checkStraightDanger(kingPosition, teamColor, 0, 1)) {
            return true;
        }

        // Check pawn danger
        if (checkPawnDanger(kingPosition, teamColor)) {
            return true;
        }

        // If none of the checks found danger, return false
        return false;
    }

    private boolean isSquareEmpty(ChessBoard board, ChessPosition square) {
        return board.getPiece(square) == null;
    }

    private boolean isInBounds(ChessPosition target) {
        int row = target.getRow();
        int col = target.getColumn();

        return row <= 8 && col <= 8 && row >= 1 && col >= 1;
    }

    public boolean checkDiagonalDanger(ChessPosition kingPosition, TeamColor kingColor, int rowDirection, int colDirection) {
        int startRow = kingPosition.getRow();
        int startCol = kingPosition.getColumn();
        while (true) {
            startRow += rowDirection;
            startCol += colDirection;
            ChessPosition target = new ChessPosition(startRow, startCol);
            if (!isInBounds(target)) {
                break;
            }
            if (!isSquareEmpty(board, target)) {
                ChessPiece piece = board.getPiece(target);
                TeamColor color = piece.getTeamColor();
                ChessPiece.PieceType type = piece.getPieceType();
                if (color == kingColor) {
                    break;
                }
                if (type == ChessPiece.PieceType.QUEEN || type == ChessPiece.PieceType.BISHOP) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkStraightDanger(ChessPosition kingPosition, TeamColor kingColor, int rowDirection, int colDirection) {
        int startRow = kingPosition.getRow();
        int startCol = kingPosition.getColumn();
        while (true) {
            startRow += rowDirection;
            startCol += colDirection;
            ChessPosition target = new ChessPosition(startRow, startCol);
            if (!isInBounds(target)) {
                break;
            }
            if (!isSquareEmpty(board, target)) {
                ChessPiece piece = board.getPiece(target);
                TeamColor color = piece.getTeamColor();
                ChessPiece.PieceType type = piece.getPieceType();
                if (color == kingColor) {
                    break;
                }
                if (type == ChessPiece.PieceType.QUEEN || type == ChessPiece.PieceType.ROOK) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkKnightDanger(ChessPosition kingPosition, TeamColor kingColor) {
        int[][] possibleMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] move : possibleMoves) {
            ChessPosition check = new ChessPosition(move[0], move[1]);
            if (isInBounds(check) && !isSquareEmpty(board, check)) {
                ChessPiece target = board.getPiece(check);
                if (target.getPieceType() == ChessPiece.PieceType.KNIGHT && target.getTeamColor() != kingColor) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkPawnDanger(ChessPosition kingPosition, TeamColor color) {
        int kingRow = kingPosition.getRow();
        int kingCol = kingPosition.getColumn();
        if (color == TeamColor.WHITE) {
            ChessPosition diagRight = new ChessPosition(kingRow + 1, kingCol + 1);
            ChessPosition diagLeft = new ChessPosition(kingRow + 1, kingCol - 1);
            if (isInBounds(diagLeft)) {
                if (!isSquareEmpty(board, diagLeft) && board.getPiece(diagLeft).getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(diagLeft).getTeamColor() != color) {
                    return true;
                }
            }
            if (isInBounds(diagRight)) {
                if (!isSquareEmpty(board, diagRight) && board.getPiece(diagRight).getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(diagRight).getTeamColor() != color) {
                    return true;
                }
            }
        } else {
            ChessPosition diagRight = new ChessPosition(kingRow - 1, kingCol + 1);
            ChessPosition diagLeft = new ChessPosition(kingRow - 1, kingCol - 1);
            if (isInBounds(diagLeft)) {
                if (!isSquareEmpty(board, diagLeft) && board.getPiece(diagLeft).getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(diagLeft).getTeamColor() != color) {
                    return true;
                }
            }
            if (isInBounds(diagRight)) {
                if (!isSquareEmpty(board, diagRight) && board.getPiece(diagRight).getPieceType() == ChessPiece.PieceType.PAWN && board.getPiece(diagRight).getTeamColor() != color) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
