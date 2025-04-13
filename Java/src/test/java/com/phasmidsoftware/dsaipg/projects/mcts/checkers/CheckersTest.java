package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckersTest {

    @Test
    public void testStartReturnsNonNullState() {
        Checkers game = new Checkers();
        State<Checkers> state = game.start();
        assertNotNull("Game should return a non-null initial state.", state);
    }

    @Test
    public void testStartReturnsCheckersState() {
        Checkers game = new Checkers();
        State<Checkers> state = game.start();
        assertTrue("Initial state should be an instance of CheckersState.", state instanceof CheckersState);
    }

    @Test
    public void testOpenerIsWhite() {
        Checkers game = new Checkers();
        int opener = game.opener();
        assertEquals("Opener should be 0 (white).", 0, opener);
    }

    @Test
    public void testInitialPlayerIsOpener() {
        Checkers game = new Checkers();
        CheckersState state = (CheckersState) game.start();
        assertEquals("Initial state player should be the opener.", game.opener(), state.player());
    }
}
