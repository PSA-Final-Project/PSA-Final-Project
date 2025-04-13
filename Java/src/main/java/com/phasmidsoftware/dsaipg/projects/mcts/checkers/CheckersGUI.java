package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CheckersGUI extends JFrame {
    private final Checkers game = new Checkers();
    private CheckersState state = (CheckersState) game.start();
    private final JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    private final JLabel statusLabel = new JLabel("Your move (White)");
    private final JButton restartButton = new JButton("Restart Game");
    private int selectedRow = -1, selectedCol = -1;
    private Set<Point> validTargets = new HashSet<>();

    public CheckersGUI() {
        setTitle("Checkers with MCTS");
        setSize(620, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        boardPanel.setPreferredSize(new Dimension(600, 600));
        add(boardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        bottomPanel.add(restartButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        restartButton.addActionListener(e -> resetGame());

        drawBoard();
        setVisible(true);
    }

    private void resetGame() {
        state = (CheckersState) game.start();
        selectedRow = selectedCol = -1;
        validTargets.clear();
        statusLabel.setText("Your move (White)");
        drawBoard();
    }

    private void drawBoard() {
        boardPanel.removeAll();
        int[][] board = state.getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel cell = new JPanel();
                boolean isLight = (row + col) % 2 == 0;
                cell.setBackground(isLight ? new Color(240, 217, 181) : new Color(181, 136, 99));

                Point thisCell = new Point(row, col);
                if (validTargets.contains(thisCell)) {
                    cell.setBackground(new Color(102, 205, 170));
                }

                int piece = board[row][col];
                if (piece == 1) {
                    cell.add(makePiece(Color.WHITE, row, col));
                } else if (piece == 2) {
                    cell.add(makePiece(Color.BLACK, -1, -1));
                }

                final int r = row, c = col;
                cell.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        handleClick(r, c);
                    }
                });
                boardPanel.add(cell);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private JLabel makePiece(Color color, int row, int col) {
        JLabel piece = new JLabel();
        piece.setOpaque(true);
        piece.setBackground(color);
        piece.setPreferredSize(new Dimension(44, 44));
        piece.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        piece.setHorizontalAlignment(SwingConstants.CENTER);
        piece.setVerticalAlignment(SwingConstants.CENTER);
        piece.setAlignmentX(Component.CENTER_ALIGNMENT);
        piece.setAlignmentY(Component.CENTER_ALIGNMENT);
        piece.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (row != -1 && col != -1) handleClick(row, col);
            }
        });
        return piece;
    }

    private void handleClick(int row, int col) {
        if (state.isTerminal()) return;

        int[][] board = state.getBoard();
        if (board[row][col] == 1) {
            selectedRow = row;
            selectedCol = col;
            validTargets = state.moves(0).stream()
                    .map(m -> (CheckersMove) m)
                    .filter(m -> m.fromRow == row && m.fromCol == col)
                    .map(m -> new Point(m.toRow, m.toCol))
                    .collect(Collectors.toSet());
            statusLabel.setText("Selected piece at (" + row + ", " + col + ") - Choose destination");
            drawBoard();
            return;
        }

        if (selectedRow != -1) {
            CheckersMove move = new CheckersMove(0, selectedRow, selectedCol, row, col);
            boolean isValid = state.moves(0).stream()
                    .map(m -> (CheckersMove) m)
                    .anyMatch(m ->
                            m.fromRow == move.fromRow && m.fromCol == move.fromCol &&
                                    m.toRow == move.toRow && m.toCol == move.toCol);

            if (isValid) {
                state = (CheckersState) state.next(move);
                selectedRow = selectedCol = -1;
                validTargets.clear();
                drawBoard();
                statusLabel.setText("AI thinking (Black)...");
                SwingUtilities.invokeLater(this::runAI);
            } else {
                statusLabel.setText("Invalid move. Re-select piece.");
                selectedRow = selectedCol = -1;
                validTargets.clear();
                drawBoard();
            }
        }
    }

    private void runAI() {
        if (state.isTerminal()) {
            endGame();
            return;
        }

        CheckersNode root = new CheckersNode(state);
        CheckersMCTS mcts = new CheckersMCTS(root);
        mcts.run(300);

        Node<Checkers> best = root.children().stream()
                .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                .orElseThrow();

        state = (CheckersState) best.state();
        drawBoard();

        if (state.isTerminal()) {
            endGame();
        } else {
            statusLabel.setText("Your move (White)");
        }
    }

    private void endGame() {
        String msg;
        if (state.winner().isPresent()) {
            int winner = state.winner().get();
            msg = "Game Over! Winner: " + (winner == 0 ? "White" : "Black");
        } else {
            msg = "Game Over! It's a draw.";
        }

        int option = JOptionPane.showOptionDialog(this,
                msg + "\nWould you like to play again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Restart Game", "Exit"},
                "Restart Game");

        if (option == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CheckersGUI::new);
    }
}
