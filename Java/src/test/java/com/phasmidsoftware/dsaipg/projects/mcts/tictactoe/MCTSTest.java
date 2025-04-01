package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class MCTSTest {

    private MCTS mcts;
    private TicTacToeNode rootNode;

    @Before
    public void setUp() {
        TicTacToe game = new TicTacToe(0L); // deterministic seed
        State<TicTacToe> startState = game.start();
        rootNode = new TicTacToeNode(startState);
        mcts = new MCTS(rootNode);
    }

    @Test
    public void testRunIncreasesPlayouts() {
        int before = rootNode.playouts();
        mcts.run(50);
        int after = rootNode.playouts();
        assertTrue("Playouts should increase after MCTS run", after > before);
    }

    @Test
    public void testBackPropagate_UpdatesStats() {
        rootNode.explore();
        Node<TicTacToe> firstChild = rootNode.children().iterator().next();
        mcts.backPropagate(firstChild, TicTacToe.X);

        assertEquals(1, firstChild.playouts());
        assertEquals(1, rootNode.playouts());

        assertTrue("Wins should be greater than 0 if player matches",
                firstChild.wins() > 0 || rootNode.wins() > 0);
    }

    @Test
    public void testBestChild_ReturnsChildWithHighestUCB1() {
        rootNode.explore();
        Collection<Node<TicTacToe>> children = rootNode.children();

        assertFalse("Children should not be empty", children.isEmpty());

        // Set parent playouts to something > 0 so log(parent.playouts) works
        rootNode.setPlayouts(100);

        Node<TicTacToe> expectedBest = null;
        double bestUCB1 = Double.NEGATIVE_INFINITY;

        for (Node<TicTacToe> child : children) {
            // Set known values
            int playouts = 10;
            double wins = Math.random() * 10;
            child.setPlayouts(playouts);
            child.setWins(wins);

            // Calculate expected UCB1 manually
            double exploitation = wins / playouts;
            double exploration = Math.sqrt(2 * Math.log(100) / playouts);
            double ucb1 = exploitation + exploration;

            if (ucb1 > bestUCB1) {
                bestUCB1 = ucb1;
                expectedBest = child;
            }
        }

        Node<TicTacToe> selected = mcts.bestChild(rootNode);
        assertNotNull("bestChild should not return null", selected);
        assertEquals("Expected the child with the highest UCB1", expectedBest, selected);
    }
}