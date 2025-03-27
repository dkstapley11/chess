package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;


    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.board = new ChessBoard();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
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
        TeamColor color = board.getPiece(startPosition).getTeamColor();
        // both players should always be able to call this, so don't use makeMove because that checks for turn
        Collection<ChessMove> initialMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : initialMoves) {
            ChessGame copyGame = new ChessGame();
            ChessBoard testBoard = new ChessBoard(board);
            copyGame.setBoard(testBoard);
            // execute the move
            copyGame.testMove(move);

            // check if that leaves it in check
            if (!copyGame.isInCheck(color)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        ChessPiece piece = board.getPiece(startPos);

        if (piece == null) {
            throw new InvalidMoveException("No piece at the starting position!");
        }
        TeamColor color = piece.getTeamColor();
        if (color != getTeamTurn()) {
            throw new InvalidMoveException("It's not your turn!");
        }
        Collection<ChessMove> validMoves = validMoves(startPos);
        System.out.println(validMoves);
        if (validMoves.contains(move))
            {
                if (move.getPromotionPiece() != null) {
                    board.addPiece(endPos, new ChessPiece(turn, move.getPromotionPiece()));
                    board.addPiece(startPos, null);
                }
                else {
                    board.addPiece(endPos, piece);
                    board.addPiece(startPos, null);
                }
            }
        else {
            throw new InvalidMoveException("That is not a valid move");
        }
        if (turn == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        }
        else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    public void testMove(ChessMove move) {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        if (move.getPromotionPiece() != null) {
            board.addPiece(endPos, new ChessPiece(turn, move.getPromotionPiece()));
            board.addPiece(startPos, null);
        }
        else {
            ChessPiece piece = board.getPiece(startPos);
            board.addPiece(endPos, piece);
            board.addPiece(startPos, null);
        }
    }


    public ChessPosition findKing(ChessBoard board, TeamColor color) {
        for (int i = 1; i <= 8; i++) {
            for (int k = 1; k <= 8; k++) {
                ChessPosition currentPos = new ChessPosition(i, k);
                ChessPiece currentPiece = board.getPiece(currentPos);
                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == color) {
                    return currentPos;
                }
            }
        }
        System.out.println(color + " King not found...");
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(board, teamColor);


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
//

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
        ChessPosition first = new ChessPosition(startRow + rowDirection, startCol + colDirection);

        if (isInBounds(first) && !isSquareEmpty(board, first) && board.getPiece(first).getPieceType() == ChessPiece.PieceType.KING) {
            return true;
        }

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

        ChessPosition first = new ChessPosition(startRow + rowDirection, startCol + colDirection);

        if (isInBounds(first) && !isSquareEmpty(board, first) && board.getPiece(first).getPieceType() == ChessPiece.PieceType.KING) {
            return true;
        }


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
        int kingRow = kingPosition.getRow();
        int kingCol = kingPosition.getColumn();
        int[][] possibleMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] move : possibleMoves) {
            ChessPosition check = new ChessPosition(kingRow + move[0], kingCol + move[1]);
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
                if (
                        !isSquareEmpty(board, diagLeft) &&
                        board.getPiece(diagLeft).getPieceType() == ChessPiece.PieceType.PAWN &&
                        board.getPiece(diagLeft).getTeamColor() != color
                )
                {
                    return true;
                }
            }
            if (isInBounds(diagRight)) {
                if (
                        !isSquareEmpty(board, diagRight) &&
                        board.getPiece(diagRight).getPieceType() ==
                        ChessPiece.PieceType.PAWN &&
                        board.getPiece(diagRight).getTeamColor() !=
                        color)
                {
                    return true;
                }
            }
        } else {
            ChessPosition diagRight = new ChessPosition(kingRow - 1, kingCol + 1);
            ChessPosition diagLeft = new ChessPosition(kingRow - 1, kingCol - 1);
            if (isInBounds(diagLeft)) {
                if (
                        !isSquareEmpty(board, diagLeft) &&
                        board.getPiece(diagLeft).getPieceType() ==
                        ChessPiece.PieceType.PAWN &&
                        board.getPiece(diagLeft).getTeamColor() !=
                        color)
                {
                    return true;
                }
            }
            if (isInBounds(diagRight)) {
                if (
                        !isSquareEmpty(board, diagRight) &&
                        board.getPiece(diagRight).getPieceType() ==
                        ChessPiece.PieceType.PAWN &&
                        board.getPiece(diagRight).getTeamColor() !=
                        color)
                {
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
        for (int i = 1; i <= 8; i++) {
            for (int k = 1; k <= 8; k++) {
                ChessPosition currentPos = new ChessPosition(i, k);
                if (!isSquareEmpty(board, currentPos) && board.getPiece(currentPos).getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(currentPos);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int k = 1; k <= 8; k++) {
                ChessPosition currentPos = new ChessPosition(i, k);
                if (!isSquareEmpty(board, currentPos) && board.getPiece(currentPos).getTeamColor() == teamColor) {
                    if (!validMoves(currentPos).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
