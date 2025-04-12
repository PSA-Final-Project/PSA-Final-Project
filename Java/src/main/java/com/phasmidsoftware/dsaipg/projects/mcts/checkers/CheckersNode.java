package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class CheckersNode implements Node<Checkers> {

    private final State<Checkers> state;
    private final ArrayList<Node<Checkers>> children;
    private Node<Checkers> parent;
    private double wins = 0;
    private int playouts;

    public CheckersMove move;
    public int visits = 0;


    public CheckersNode(State<Checkers> state) {
        this.state = state;
        this.children = new ArrayList<>();
        this.parent = null;
        initializeNodeData();
    }

    private void initializeNodeData() {
        if (isLeaf()) {
            this.playouts = 1;
            Optional<Integer> winner = state.winner();
            if (winner.isPresent()) {
                this.wins = 2.0;
            } else {
                this.wins = 1.0;
            }
        } else {
            this.playouts = 0;
            this.wins = 0.0;
        }
    }

    @Override
    public double wins() {
        return wins;
    }

    @Override
    public void setWins(double wins) {
        this.wins = wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    @Override
    public void setPlayouts(int playouts) {
        this.playouts = playouts;
    }

    @Override
    public Node<Checkers> getParent() {
        return parent;
    }

    @Override
    public void setParent(Node<Checkers> parent) {
        this.parent = parent;
    }

    @Override
    public boolean isLeaf() {
        return state.isTerminal();
    }

    @Override
    public State<Checkers> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == state.game().opener(); // "white" is the opener
    }

    @Override
    public Collection<Node<Checkers>> children() {
        return children;
    }

    @Override
    public void addChild(State<Checkers> state) {
        if (state == null) throw new IllegalArgumentException("Null state cannot be added as child");
        CheckersNode child = new CheckersNode(state);
        child.setParent(this);
        children.add(child);
    }

    @Override
    public void backPropagate() {
        playouts = 0;
        wins = 0;
        for (Node<Checkers> child : children) {
            wins += child.wins();
            playouts += child.playouts();
        }
    }
}
