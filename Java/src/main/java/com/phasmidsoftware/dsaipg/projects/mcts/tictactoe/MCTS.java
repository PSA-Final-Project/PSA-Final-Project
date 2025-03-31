/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Class to represent a Monte Carlo Tree Search for TicTacToe.
 */
public class MCTS {


    private Node<TicTacToe> root;

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    public Node<TicTacToe> getRoot() {
        return root;
    }
    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {
            Node<TicTacToe> node = select(root);
            int result = simulate(node);
            backPropagate(node, result);
        }
    }

    Node<TicTacToe> select(Node<TicTacToe> node) {
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

    Node<TicTacToe> bestChild(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparingDouble(this::ucb1))
                .orElseThrow(() -> new IllegalStateException("No best child found"));
    }

    private double ucb1(Node<TicTacToe> node) {
        if (node.playouts() == 0) return Double.POSITIVE_INFINITY;
        double c = Math.sqrt(2);
        return (node.wins() / node.playouts()) +
                c * Math.sqrt(Math.log(node.getParent().playouts()) / node.playouts());
    }

    int simulate(Node<TicTacToe> node) {
        State<TicTacToe> state = node.state();
        Random random = new Random();
        while (!state.isTerminal()) {
            List<Move<TicTacToe>> moves = new ArrayList<>(state.moves(state.player()));
            Move<TicTacToe> move = moves.get(random.nextInt(moves.size()));
            state = state.next(move);
        }
        return state.winner().orElse(-1);
    }

    void backPropagate(Node<TicTacToe> node, int result) {
        while (node != null) {
            node.setPlayouts(node.playouts() + 1);

            int movePlayer = (node.getParent() == null) ? -1 : node.getParent().state().player();

            if (result == -1) {
                node.setWins(node.wins() + 0.0);
            } else if (movePlayer == result) {
                node.setWins(node.wins() + 3);
            }

            node = node.getParent();
        }
    }
}