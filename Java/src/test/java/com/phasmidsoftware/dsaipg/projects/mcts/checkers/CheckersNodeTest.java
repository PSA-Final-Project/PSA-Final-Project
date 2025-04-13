package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class CheckersNodeTest {

    private Checkers game;
    private CheckersState initialState;
    private CheckersNode rootNode;

    @Before
    public void setUp() {
        game = new Checkers();
        initialState = (CheckersState) game.start();
        rootNode = new CheckersNode(initialState);
    }


    @Test
    public void testWinsAndPlayouts() {
        rootNode.setWins(5.0);
        rootNode.setPlayouts(10);

        assertEquals("Wins should be 5.0", 5.0, rootNode.wins(), 0.001);
        assertEquals("Playouts should be 10", 10, rootNode.playouts());
    }

    @Test
    public void testAddChild() {
        CheckersState childState = (CheckersState) game.start();  // Using the initial state again for simplicity
        rootNode.addChild(childState);

        assertEquals("There should be one child", 1, rootNode.children().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddChildWithNullState() {
        rootNode.addChild(null);  // This should throw an exception
    }

    @Test
    public void testBackPropagate() {
        CheckersNode childNode = new CheckersNode(initialState);
        childNode.setWins(10.0);
        childNode.setPlayouts(5);

        rootNode.addChild(initialState);
        rootNode.backPropagate();

        // Ensure that the rootNode correctly sums up the wins and playouts of its children
        assertEquals("Backpropagation should sum wins of children", 0.0, rootNode.wins(), 0.001);
        assertEquals("Backpropagation should sum playouts of children", 0, rootNode.playouts());
    }

    @Test
    public void testIsLeafForTerminalState() {
        // Creating a mock terminal state
        CheckersState terminalState = new CheckersState() {
            @Override
            public boolean isTerminal() {
                return true;
            }

            @Override
            public Optional<Integer> winner() {
                return Optional.empty();  // Draw
            }
        };

        CheckersNode terminalNode = new CheckersNode(terminalState);
        assertTrue("Terminal node should be a leaf", terminalNode.isLeaf());
    }

    @Test
    public void testIsLeafForNonTerminalState() {
        CheckersNode nonTerminalNode = new CheckersNode(initialState);
        assertFalse("Non-terminal node should not be a leaf", nonTerminalNode.isLeaf());
    }

    @Test
    public void testWhite() {
        // Assuming player 0 (white) is the opener
        assertTrue("White should be the opener", rootNode.white());
    }

    @Test
    public void testSetParent() {
        CheckersNode childNode = new CheckersNode(initialState);
        childNode.setParent(rootNode);
        assertEquals("Parent of the child node should be root", rootNode, childNode.getParent());
    }
}
