package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Unit tests for the MCTS class.
 */
public class MCTSTest {

    private MCTS mcts;
    private Node<TicTacToe> rootNode;

    @Before
    public void setUp() {
        State<TicTacToe> initialState = new TicTacToe().start();
        rootNode = new TicTacToeNode(initialState);

        mcts = new MCTS(rootNode);
    }

    /**
     * Test the select method on a root node which is not a leaf.
     * By default, the root node has no children initially, so "explore" should be called.
     */
    @Test
    public void testSelect_ExploresNodeWhenNoChildren() {
        assertFalse("Root node should not be a leaf on an empty board", rootNode.isLeaf());

        assertTrue("Initially, root node should have zero children", rootNode.children().isEmpty());

        Node<TicTacToe> selectedNode = mcts.select(rootNode);

        assertFalse("Children should have been created by explore()", rootNode.children().isEmpty());

        assertNotNull("Selected node should not be null", selectedNode);
    }

    /**
     * Test the simulate method to ensure it always ends in a terminal state.
     * We won't assert a winner because TicTacToe can end in a draw or a win.
     */
    @Test
    public void testSimulate_EndsInTerminalState() {
        int result = mcts.simulate(rootNode);

        assertTrue("Result should be -1 (draw) or 0 (O) or 1 (X)",
                Arrays.asList(-1, 0, 1).contains(result));
    }

    /**
     * Test the backPropagate method by creating a small chain of nodes
     * and verifying that playouts and wins are incremented correctly along the path.
     */
    @Test
    public void testBackPropagate_UpdatesStats() {
        rootNode.explore();
        Node<TicTacToe> firstChild = rootNode.children().iterator().next();

        int fakeResult = TicTacToe.X;
        mcts.backPropagate(firstChild, fakeResult);

        assertEquals("Child node playouts should be 1", 1, firstChild.playouts());
        assertEquals("Root node playouts should be 1", 1, rootNode.playouts());

        double childWins = firstChild.wins();
        double rootWins = rootNode.wins();

        assertNotEquals("Child node wins should have changed from 0.0", 0.0, childWins, 1e-9);
        assertTrue("Root node wins can remain 0 if parent's move doesn't match the result",
                rootNode.wins() == 0.0 || rootNode.wins() == 3.0);
    }
}