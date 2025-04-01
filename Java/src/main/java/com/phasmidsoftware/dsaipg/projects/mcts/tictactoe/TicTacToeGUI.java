package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicTacToeGUI extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private TicTacToe game;
    private TicTacToe.TicTacToeState currentState;
    private MCTS mcts;
    private TicTacToeNode rootNode;

    private JButton resetButton;
    private JButton aiMoveButton;

    public TicTacToeGUI() {
        game = new TicTacToe();
        currentState = (TicTacToe.TicTacToeState) game.start();
        rootNode = new TicTacToeNode(currentState);
        mcts = new MCTS(rootNode);

        setTitle("Tic-Tac-Toe");
        setLayout(new BorderLayout());
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton button = new JButton(".");
                button.setFont(new Font("Arial", Font.PLAIN, 40));
                button.setFocusPainted(false);
                button.setEnabled(true);
                final int x = i, y = j;
                button.addActionListener(e -> playerMove(x, y));
                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        resetButton = new JButton("Reset Game");
        resetButton.addActionListener(e -> resetGame());

        aiMoveButton = new JButton("AI Move");
        aiMoveButton.addActionListener(e -> aiMove());

        controlPanel.add(resetButton);
        controlPanel.add(aiMoveButton);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Method to count the number of moves played
    private int countMoves() {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!buttons[i][j].getText().equals(".")) {
                    count++;
                }
            }
        }
        return count;
    }

    private void playerMove(int x, int y) {
        if (currentState.isTerminal() || !buttons[x][y].getText().equals(".")) return;

        // Check if it's X's turn
        if (countMoves() % 2 != 0) {
            System.out.println("It's not Player X's turn!");
            return;
        }

        // Player X makes a move
        currentState = (TicTacToe.TicTacToeState) currentState.next(new TicTacToe.TicTacToeMove(TicTacToe.X, x, y));
        buttons[x][y].setText("X");
        buttons[x][y].setEnabled(false);

        if (currentState.isTerminal()) {
            displayGameOver();
        }
    }

    private void aiMove() {
        if (currentState.isTerminal()) return;

        // Ensure it's O's turn before AI plays
        if (countMoves() % 2 == 0) {
            System.out.println("It's not AI's turn!");
            return;
        }

        rootNode = new TicTacToeNode(currentState);
        mcts = new MCTS(rootNode);
        mcts.run(1000);

        Node<TicTacToe> bestMoveNode = mcts.bestChild(rootNode);
        if (bestMoveNode == null) {
            System.out.println("No best move found for AI!");
            return;
        }

        TicTacToe.TicTacToeState bestMoveState = (TicTacToe.TicTacToeState) bestMoveNode.state();
        if (bestMoveState == null) {
            System.out.println("AI move state is invalid!");
            return;
        }

        // Get a list of available (empty) spots on the board
        List<int[]> emptySpots = getEmptySpots();
        if (emptySpots.isEmpty()) {
            System.out.println("No available moves for AI!");
            return;
        }

        // Choose the best move based on the MCTS result (e.g., using the first available move)
        for (int[] move : emptySpots) {
            int row = move[0];
            int col = move[1];

            // Make sure the move is valid
            if (!buttons[row][col].getText().equals(".")) {
                System.out.println("AI tried to move to an occupied position!");
                continue;
            }

            // Update state with AI's move
            currentState = (TicTacToe.TicTacToeState) currentState.next(new TicTacToe.TicTacToeMove(TicTacToe.O, row, col));

            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                buttons[row][col].setText("O");
                buttons[row][col].setEnabled(false);

                if (currentState.isTerminal()) {
                    displayGameOver();
                }
            });
            return; // Exit after making a valid move
        }

        System.out.println("AI couldn't find a valid move!");
    }

    // Helper method to get empty spots on the board
    private List<int[]> getEmptySpots() {
        List<int[]> emptySpots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals(".")) {
                    emptySpots.add(new int[]{i, j});
                }
            }
        }
        return emptySpots;
    }


    private void displayGameOver() {
        String winner = "It's a draw!";
        Optional<Integer> gameResult = currentState.winner();
        if (gameResult.isPresent()) {
            winner = (gameResult.get() == TicTacToe.X) ? "Player X Wins!" : "Player O Wins!";
        }

        JOptionPane.showMessageDialog(this, winner, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        resetGame();
    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(".");
                buttons[i][j].setEnabled(true);
            }
        }
        game = new TicTacToe();
        currentState = (TicTacToe.TicTacToeState) game.start();
        rootNode = new TicTacToeNode(currentState);
        mcts = new MCTS(rootNode);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGUI::new);
    }
}
