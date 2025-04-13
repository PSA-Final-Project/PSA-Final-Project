package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

public class CheckersMainTestHelper {
    public static void printBoard(CheckersState state) {
        System.out.println("Current board:");
        int[][] board = state.getBoard();
        for (int[] row : board) {
            for (int cell : row) {
                char symbol = switch (cell) {
                    case 1 -> 'W';
                    case 2 -> 'B';
                    default -> '.';
                };
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}