package ui;
import chess.*;
import static ui.EscapeSequences.*;

public class ChessBoardPrinter {

    public static void printBoard(ChessBoard chessBoard, boolean whitePerspective) {
        System.out.println(getFileLabels(whitePerspective));

        if (whitePerspective) {
            for (int rank = 8; rank >= 1; rank--) {
                printRankLine(chessBoard, rank, whitePerspective);
            }
        } else {
            for (int rank = 1; rank <= 8; rank++) {
                printRankLine(chessBoard, rank, whitePerspective);
            }
        }

        System.out.println(getFileLabels(whitePerspective));
    }

    public static void printStartBoard(boolean whitePerspective) {
        printBoard(new ChessBoard(), whitePerspective);
    }

    private static void printRankLine(ChessBoard chessBoard, int rank, boolean whitePerspective) {
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE + " " + rank + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);

        if (whitePerspective) {
            for (int file = 1; file <= 8; file++) {
                String squareBg = getSquareBgColor(rank, file);
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(rank, file));
                String pieceStr = getPieceIcon(piece);
                // Each square is printed with background and centered piece.
                System.out.print(squareBg + " " + pieceStr + " " + RESET_BG_COLOR);
            }
        } else {
            for (int file = 8; file >= 1; file--) {
                String squareBg = getSquareBgColor(rank, file);
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(rank, file));
                String pieceStr = getPieceIcon(piece);
                System.out.print(squareBg + " " + pieceStr + " " + RESET_BG_COLOR);
            }
        }

        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE + " " + rank + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    /**
     * Builds a string of file labels (a-h) for the top or bottom of the board.
     *
     * @param whitePerspective if true, labels in order a-h; if false, in reverse.
     * @return the formatted file label string.
     */
    private static String getFileLabels(boolean whitePerspective) {
        StringBuilder labels = new StringBuilder();
        labels.append("   ");
        if (whitePerspective) {
            for (char file = 'a'; file <= 'h'; file++) {
                labels.append("  ").append(file).append(" ");
            }
        } else {
            for (char file = 'h'; file >= 'a'; file--) {
                labels.append("  ").append(file).append(" ");
            }
        }
        return labels.toString();
    }

    private static String getSquareBgColor(int rank, int file) {
        // Alternate colors based on the sum of rank and file.
        return ((rank + file) % 2 == 0) ? SET_BG_COLOR_RED : SET_BG_COLOR_LIGHT_GREY;
    }

    private static String getPieceIcon(ChessPiece piece) {
        if (piece == null) {
            return "  "; // Two spaces for an empty square.
        }
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            default -> "  ";
        };
    }
}
