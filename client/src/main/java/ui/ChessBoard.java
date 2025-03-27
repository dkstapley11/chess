package ui;

import static ui.EscapeSequences.*;

public class ChessBoard {
    private static final int SIZE = 8;

    public static void drawBoard(boolean whitePerspective) {
        // Create a board array to hold the piece strings.
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

        // Determine row iteration based on perspective.
        int startRow, endRow, step;
        if (whitePerspective) {
            startRow = 0; endRow = SIZE; step = 1;
        } else {
            startRow = SIZE - 1; endRow = -1; step = -1;
        }

        // Draw the board with rank numbers and file letters.
        // Note: board row 0 corresponds to rank 8 (white perspective) and vice versa.
        for (int i = startRow; i != endRow; i += step) {
            // Compute rank number for display.
            int rank = whitePerspective ? 8 - i : i + 1;
            System.out.print(rank + " "); // left border with rank

            // For columns, reverse order for black perspective.
            for (int j = 0; j < SIZE; j++) {
                int col = whitePerspective ? j : (SIZE - 1 - j);
                System.out.print(board[i][col]);
            }
            System.out.println(" " + rank); // right border with rank
        }

        // Print file letters at the bottom.
        if (whitePerspective) {
            System.out.println("   a  b  c  d  e  f  g  h");
        } else {
            System.out.println("   h  g  f  e  d  c  b  a");
        }
    }
}

