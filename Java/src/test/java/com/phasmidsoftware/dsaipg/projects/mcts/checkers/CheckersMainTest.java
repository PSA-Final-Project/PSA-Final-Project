package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckersMainTest {

    @Test
    public void testGameStartsCorrectly() {
        Checkers game = new Checkers();
        CheckersState state = (CheckersState) game.start();

        assertNotNull("Initial state should not be null", state);
        assertEquals("Initial player should be the opener (WHITE)", game.opener(), state.player());
    }

    @Test
    public void testOneMoveAdvancement() {
        Checkers game = new Checkers();
        CheckersState state = (CheckersState) game.start();

        CheckersNode node = new CheckersNode(state);
        CheckersMCTS mcts = new CheckersMCTS(node);
        mcts.run(100);

        Node<Checkers> bestChild = node.children().stream()
                .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                .orElse(null);

        assertNotNull("Best child should not be null", bestChild);
        CheckersState newState = (CheckersState) bestChild.state();

        assertNotEquals("Game should have progressed to next state", state, newState);
        assertNotEquals("Player should switch after move", state.player(), newState.player());
    }

    @Test
    public void testGameCanReachTerminalState() {
        Checkers game = new Checkers();
        CheckersState currentState = (CheckersState) game.start();
        int maxMoves = 200;
        int count = 0;

        while (!currentState.isTerminal() && count < maxMoves) {
            CheckersNode node = new CheckersNode(currentState);
            CheckersMCTS mcts = new CheckersMCTS(node);
            mcts.run(50);

            Node<Checkers> bestChild = node.children().stream()
                    .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                    .orElse(null);

            if (bestChild == null) break;
            currentState = (CheckersState) bestChild.state();
            count++;
        }

        assertTrue("Game should eventually reach a terminal state or hit move cap", currentState.isTerminal() || count >= maxMoves);
    }

    @Test
    public void testPrintBoardDoesNotThrow() {
        Checkers game = new Checkers();
        CheckersState state = (CheckersState) game.start();

        try {
            CheckersMainTestHelper.printBoard(state); // use helper to call print method
        } catch (Exception e) {
            fail("printBoard should not throw exception: " + e.getMessage());
        }
    }
}
