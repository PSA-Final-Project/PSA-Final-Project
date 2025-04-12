package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CheckersMCTS {

    private Node<Checkers> root;
    private final Random random = new Random();

    public CheckersMCTS(Node<Checkers> root) {
        this.root = root;
    }

    public Node<Checkers> getRoot() {
        return root;
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {
            Node<Checkers> node = select(root);
            int result = simulate(node);
            backPropagate(node, result);
        }
    }

    Node<Checkers> select(Node<Checkers> node) {
        while (!node.isLeaf()) {
            if (!node.children().isEmpty()) {
                node = bestChild(node);
            } else {
                node.explore();
                return node;
            }
        }
        return node;
    }

    Node<Checkers> bestChild(Node<Checkers> node) {
        return node.children().stream()
                .max(Comparator.comparingDouble(this::ucb1))
                .orElseThrow(() -> new IllegalStateException("No best child found"));
    }

    private double ucb1(Node<Checkers> node) {
        if (node.playouts() == 0) return Double.POSITIVE_INFINITY;
        double c = Math.sqrt(2);
        return (node.wins() / node.playouts()) +
                c * Math.sqrt(Math.log(node.getParent().playouts()) / node.playouts());
    }

    int simulate(Node<Checkers> node) {
        State<Checkers> state = node.state();
        while (!state.isTerminal()) {
            List<Move<Checkers>> moves = new ArrayList<>(state.moves(state.player()));
            if (moves.isEmpty()) break;
            Move<Checkers> move = moves.get(random.nextInt(moves.size()));
            state = state.next(move);
        }
        return state.winner().orElse(-1);
    }

    void backPropagate(Node<Checkers> node, int result) {
        while (node != null) {
            node.setPlayouts(node.playouts() + 1);

            int movePlayer = (node.getParent() == null) ? -1 : node.getParent().state().player();

            if (result == -1) {
                node.setWins(node.wins() + 0.5); // draw
            } else if (movePlayer == result) {
                node.setWins(node.wins() + 1);
            }

            node = node.getParent();
        }
    }
}
