package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

class CheckersState implements State<Checkers> {
    private final Checkers game;
    private final int[][] board; // 0 = empty, 1 = white, 2 = black
    private final int currentPlayer; // 0 = white, 1 = black
    private final Random random;

    public CheckersState(Checkers game, int[][] board, int currentPlayer, Random random) {
        this.game = game;
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.random = random;
    }

    public CheckersState() {
        this.game = new Checkers();
        this.board = initializeBoard();
        this.currentPlayer = 0;
        this.random = new Random();
    }

    private int[][] initializeBoard() {
        int[][] newBoard = new int[8][8];
        for (int row = 0; row < 3; row++) {
            for (int col = (row + 1) % 2; col < 8; col += 2) {
                newBoard[row][col] = 2; // Black pieces
            }
        }
        for (int row = 5; row < 8; row++) {
            for (int col = (row + 1) % 2; col < 8; col += 2) {
                newBoard[row][col] = 1; // White pieces
            }
        }
        return newBoard;
    }

    @Override
    public Checkers game() {
        return game;
    }

    @Override
    public boolean isTerminal() {
        return moves(currentPlayer).isEmpty();
    }

    @Override
    public int player() {
        return currentPlayer;
    }

    @Override
    public Optional<Integer> winner() {
        boolean whiteHasMoves = !moves(0).isEmpty();
        boolean blackHasMoves = !moves(1).isEmpty();

        boolean whiteHasPieces = hasPieces(1); // white = 1
        boolean blackHasPieces = hasPieces(2); // black = 2

        if ((!whiteHasMoves || !whiteHasPieces) && (!blackHasMoves || !blackHasPieces)) {
            return Optional.empty(); // draw
        } else if (!whiteHasMoves || !whiteHasPieces) {
            return Optional.of(1); // black wins
        } else if (!blackHasMoves || !blackHasPieces) {
            return Optional.of(0); // white wins
        }

        return Optional.empty(); // game is ongoing
    }

    private boolean hasPieces(int pieceCode) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == pieceCode) return true;
            }
        }
        return false;
    }

    @Override
    public Random random() {
        return random;
    }

    @Override
    public Collection<Move<Checkers>> moves(int player) {
        List<Move<Checkers>> result = new ArrayList<>();
        int pieceCode = (player == 0) ? 1 : 2;         // 1 = White, 2 = Black
        int direction = (player == 0) ? -1 : 1;        // White moves up, Black moves down

        // First, find capturing moves
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == pieceCode) {
                    for (int dCol = -2; dCol <= 2; dCol += 4) {
                        int newRow = row + 2 * direction;
                        int newCol = col + dCol;
                        int midRow = row + direction;
                        int midCol = col + dCol / 2;

                        if (isInBounds(newRow, newCol)
                                && board[newRow][newCol] == 0
                                && board[midRow][midCol] != 0
                                && board[midRow][midCol] != pieceCode) {
                            result.add(new CheckersMove(player, row, col, newRow, newCol));
                        }
                    }
                }
            }
        }

        // If capturing moves exist, return only those
        if (!result.isEmpty()) return result;

        // Otherwise, normal (non-capturing) moves
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == pieceCode) {
                    for (int dCol = -1; dCol <= 1; dCol += 2) {
                        int newRow = row + direction;
                        int newCol = col + dCol;
                        if (isInBounds(newRow, newCol) && board[newRow][newCol] == 0) {
                            result.add(new CheckersMove(player, row, col, newRow, newCol));
                        }
                    }
                }
            }
        }
        return result;
    }

    public Collection<Move<Checkers>> movesHuman(int player) {
        List<Move<Checkers>> result = new ArrayList<>();
        int pieceCode = (player == 0) ? 1 : 2;         // 1 = White, 2 = Black
        int direction = (player == 0) ? -1 : 1;        // White moves up, Black moves down

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == pieceCode) {
                    // Non-capturing diagonal moves (1 step)
                    for (int dCol = -1; dCol <= 1; dCol += 2) {
                        int newRow = row + direction;
                        int newCol = col + dCol;
                        if (isInBounds(newRow, newCol) && board[newRow][newCol] == 0) {
                            result.add(new CheckersMove(player, row, col, newRow, newCol));
                        }
                    }

                    // Capturing diagonal moves (2 steps)
                    for (int dCol = -2; dCol <= 2; dCol += 4) {
                        int newRow = row + 2 * direction;
                        int newCol = col + dCol;
                        int midRow = row + direction;
                        int midCol = col + dCol / 2;

                        if (isInBounds(newRow, newCol)
                                && board[newRow][newCol] == 0
                                && board[midRow][midCol] != 0
                                && board[midRow][midCol] != pieceCode) {
                            result.add(new CheckersMove(player, row, col, newRow, newCol));
                        }
                    }
                }
            }
        }

        return result;
    }


    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    @Override
    public State<Checkers> next(Move<Checkers> move) {
        CheckersMove m = (CheckersMove) move;
        int[][] newBoard = copyBoard();
        int movingPiece = newBoard[m.fromRow][m.fromCol];
        newBoard[m.fromRow][m.fromCol] = 0;
        newBoard[m.toRow][m.toCol] = movingPiece;

        // Capturing logic
        int rowDiff = m.toRow - m.fromRow;
        int colDiff = m.toCol - m.fromCol;
        if (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 2) {
            int capturedRow = m.fromRow + rowDiff / 2;
            int capturedCol = m.fromCol + colDiff / 2;
            newBoard[capturedRow][capturedCol] = 0;
        }

        return new CheckersState(game, newBoard, 1 - currentPlayer, random);
    }

    private int[][] copyBoard() {
        int[][] newBoard = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, 8);
        }
        return newBoard;
    }

    public int[][] getBoard() {
        return board;
    }
}

