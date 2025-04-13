package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class CheckersMCTSTest {

    private Checkers game;
    private CheckersState initialState;
    private CheckersNode rootNode;
    private CheckersMCTS mcts;

    @Before
    public void setUp() {
        game = new Checkers();
        initialState = (CheckersState) game.start();
        rootNode = new CheckersNode(initialState);
        mcts = new CheckersMCTS(rootNode);
    }

    @Test
    public void testConstructorAndGetter() {
        assertEquals("Root node should match the one provided", rootNode, mcts.getRoot());
    }

    @Test
    public void testRunMCTSAddsPlayouts() {
        mcts.run(10);
        assertTrue("After running, root node should have playouts > 0", rootNode.playouts() > 0);
    }

    @Test
    public void testSelectReturnsNode() {
        Node<Checkers> selected = mcts.select(rootNode);
        assertNotNull("Select should return a non-null node", selected);
    }

    @Test
    public void testSimulateReturnsValidPlayerOrDraw() {
        CheckersNode node = new CheckersNode(initialState);
        int result = mcts.simulate(node);
        assertTrue("Simulate should return -1 (draw), 0 (white), or 1 (black)", result == -1 || result == 0 || result == 1);
    }

    @Test
    public void testBackPropagateUpdatesPlayoutsAndWins() {
        CheckersNode node = new CheckersNode(initialState);
        node.setPlayouts(0);
        node.setWins(0);
        mcts.backPropagate(node, 0); // Suppose WHITE (player 0) wins

        assertTrue("Playouts should be incremented", node.playouts() > 0);
        assertTrue("Wins should be incremented or adjusted", node.wins() >= 0);
    }

    @Test
    public void testBestChildAfterRun() {
        mcts.run(50);
        Collection<Node<Checkers>> children = rootNode.children();
        assertFalse("Root node should have children after running MCTS", children.isEmpty());
        Node<Checkers> bestChild = children.stream()
                .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                .orElse(null);
        assertNotNull("bestChild should not be null", bestChild);
    }


    @Test
    public void testFullSimulationGameTerminates() {
        CheckersState state = initialState;
        int maxMoves = 200;
        int moveCount = 0;

        while (!state.isTerminal() && moveCount < maxMoves) {
            CheckersNode node = new CheckersNode(state);
            CheckersMCTS simMcts = new CheckersMCTS(node);
            simMcts.run(30);

            Collection<Node<Checkers>> children = node.children();
            if (children.isEmpty()) break;

            Node<Checkers> best = children.stream()
                    .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                    .orElse(null);

            if (best == null) break;

            state = (CheckersState) best.state();
            moveCount++;
        }

        assertTrue("Simulation should reach terminal or max moves", state.isTerminal() || moveCount >= maxMoves);
    }
}
