package ui;

import static ui.EscapeSequences.*;

public class ChessBoard {
    private static final int SIZE = 8;

    public static void drawBoard(boolean whitePerspective) {
        // Create a board array for the pieces.
        String[][] board = new String[SIZE][SIZE];

        // Initialize all squares as EMPTY.
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }

        // Set up white pieces (bottom row & pawn row).
        board[7][0] = WHITE_ROOK;
        board[7][1] = WHITE_KNIGHT;
        board[7][2] = WHITE_BISHOP;
        board[7][3] = WHITE_QUEEN;
        board[7][4] = WHITE_KING;
        board[7][5] = WHITE_BISHOP;
        board[7][6] = WHITE_KNIGHT;
        board[7][7] = WHITE_ROOK;
        for (int j = 0; j < SIZE; j++) {
            board[6][j] = WHITE_PAWN;
        }

        // Set up black pieces (top row & pawn row).
        board[0][0] = BLACK_ROOK;
        board[0][1] = BLACK_KNIGHT;
        board[0][2] = BLACK_BISHOP;
        board[0][3] = BLACK_QUEEN;
        board[0][4] = BLACK_KING;
        board[0][5] = BLACK_BISHOP;
        board[0][6] = BLACK_KNIGHT;
        board[0][7] = BLACK_ROOK;
        for (int j = 0; j < SIZE; j++) {
            board[1][j] = BLACK_PAWN;
        }

        // Determine row iteration order based on perspective.
        int startRow, endRow, step;
        if (whitePerspective) {
            startRow = 0; endRow = SIZE; step = 1;
        } else {
            startRow = SIZE - 1; endRow = -1; step = -1;
        }

        // Print top file labels.
        System.out.println(getFileLabels(whitePerspective));

        // Print each rank.
        for (int i = startRow; i != endRow; i += step) {
            // Compute rank number for display.
            int rank = whitePerspective ? 8 - i : i + 1;
            // Print left rank border.
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE + " " + rank + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
            // Print each square.
            for (int j = 0; j < SIZE; j++) {
                // Reverse column order for black perspective.
                int col = whitePerspective ? j : (SIZE - 1 - j);
                String squareBg = getSquareBgColor(rank, col + 1);
                String piece = board[i][col];
                // Each square is printed with its background and centered piece.
                System.out.print(squareBg + " " + piece + " " + RESET_BG_COLOR);
            }
            // Print right rank border.
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE + " " + rank + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
        }

        // Print bottom file labels.
        System.out.println(getFileLabels(whitePerspective));
    }

    /**
     * Returns a string with the file labels (a-h) in a formatted row.
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

    /**
     * Returns a background color for a square based on its rank and file (1-indexed).
     * This example alternates between two colors for a checkerboard pattern.
     */
    private static String getSquareBgColor(int rank, int file) {
        // Alternate colors based on the sum of rank and file.
        if ((rank + file) % 2 == 0) {
            return SET_BG_COLOR_RED;
        } else {
            return SET_BG_COLOR_LIGHT_GREY;
        }
    }
}
