package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;

public class CheckersMain {
    public static void main(String[] args) {
        //Create a new game
        Checkers game = new Checkers();
        CheckersState initialState = (CheckersState) game.start();

        //Create root node
        CheckersNode root = new CheckersNode(initialState);

        //Set up MCTS
        CheckersMCTS mcts = new CheckersMCTS(root);

        //Play the game until terminal state
        CheckersState currentState = initialState;
        int moveCount = 1;

        while (!currentState.isTerminal()) {
            System.out.println("Move #" + moveCount + " | Player: " + (currentState.player() == game.opener() ? "WHITE" : "BLACK"));
            // Run MCTS for current state
            CheckersNode currentNode = new CheckersNode(currentState);
            CheckersMCTS currentMCTS = new CheckersMCTS(currentNode);
            currentMCTS.run(300);

            // Choose the best move (child with most playouts)
            Node<Checkers> bestChild = currentNode.children().stream()
                    .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                    .orElseThrow(() -> new IllegalStateException("No moves available"));

            currentState = (CheckersState) bestChild.state();
            printBoard((CheckersState) bestChild.state());
            moveCount++;
        }

        System.out.println("Game Over!");
        currentState.winner().ifPresentOrElse(
                winner -> System.out.println("Winner: " + (winner == game.opener() ? "WHITE" : "BLACK")),
                () -> System.out.println("It's a draw!")
        );
    }

    private static void printBoard(CheckersState state) {
        System.out.println("Current board:");
        int[][] board = state.getBoard();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                int cell = board[row][col];
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

