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
    private JComboBox<String> difficultyBox; // ✅ ADDED

    private JButton resetButton;
    private JButton aiMoveButton;

    public TicTacToeGUI() {
        game = new TicTacToe();
        currentState = (TicTacToe.TicTacToeState) game.start();
        rootNode = new TicTacToeNode(currentState);
        mcts = new MCTS(rootNode);

        setTitle("Tic-Tac-Toe");
        setLayout(new BorderLayout());
        setSize(400, 450);
        setLocationRelativeTo(null);
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

        // ✅ Difficulty dropdown added here
        String[] levels = {"Easy", "Medium", "Hard"};
        difficultyBox = new JComboBox<>(levels);
        controlPanel.add(resetButton);
        controlPanel.add(aiMoveButton);
        controlPanel.add(new JLabel("Difficulty:"));
        controlPanel.add(difficultyBox);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

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

        if (countMoves() % 2 != 0) {
            System.out.println("It's not Player X's turn!");
            return;
        }

        currentState = (TicTacToe.TicTacToeState) currentState.next(new TicTacToe.TicTacToeMove(TicTacToe.X, x, y));
        buttons[x][y].setText("X");
        buttons[x][y].setEnabled(false);

        if (currentState.isTerminal()) {
            displayGameOver();
        }
    }

    private void aiMove() {
        if (currentState.isTerminal()) return;

        if (countMoves() % 2 == 0) {
            System.out.println("It's not AI's turn!");
            return;
        }

        // ✅ Get difficulty level
        String selectedLevel = (String) difficultyBox.getSelectedItem();
        int iterations = switch (selectedLevel) {
            case "Easy" -> 0;          // Random
            case "Medium" -> 1000;
            case "Hard" -> 5000;
            default -> 1000;
        };

        if (iterations == 0) {
            List<int[]> emptySpots = getEmptySpots();
            if (!emptySpots.isEmpty()) {
                int[] move = emptySpots.get(new java.util.Random().nextInt(emptySpots.size()));
                int row = move[0], col = move[1];
                currentState = (TicTacToe.TicTacToeState) currentState.next(
                        new TicTacToe.TicTacToeMove(TicTacToe.O, row, col));
                buttons[row][col].setText("O");
                buttons[row][col].setEnabled(false);
                if (currentState.isTerminal()) {
                    displayGameOver();
                }
            }
            return;
        }

        // Medium or Hard difficulty: MCTS
        rootNode = new TicTacToeNode(currentState);
        mcts = new MCTS(rootNode);
        mcts.run(iterations);

        Node<TicTacToe> bestMoveNode = mcts.bestChild(rootNode);
        if (bestMoveNode == null) {
            System.out.println("No best move found for AI!");
            return;
        }

        Position before = currentState.position();
        Position after = ((TicTacToe.TicTacToeState) bestMoveNode.state()).position();
        int[] moveCoords = findMoveCoordinates(before, after);
        if (moveCoords == null) {
            System.out.println("AI move could not be identified.");
            return;
        }

        int row = moveCoords[0], col = moveCoords[1];
        currentState = (TicTacToe.TicTacToeState) bestMoveNode.state();
        buttons[row][col].setText("O");
        buttons[row][col].setEnabled(false);

        if (currentState.isTerminal()) {
            displayGameOver();
        }
    }

    private int[] findMoveCoordinates(Position before, Position after) {
        int[][] g1 = before.getGrid();
        int[][] g2 = after.getGrid();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (g1[i][j] != g2[i][j]) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

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
            winner = (gameResult.get() == TicTacToe.X) ? "Player X Wins!" : "AI Wins!";
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