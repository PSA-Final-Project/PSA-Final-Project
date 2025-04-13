package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckersMoveTest {

    private CheckersMove move;

    @Before
    public void setUp() {
        // Initializing with a sample move for player 0 (white), from (1,1) to (2,2)
        move = new CheckersMove(0, 1, 1, 2, 2);
    }

    @Test
    public void testConstructor() {
        // Verify that the constructor correctly initializes the values.
        assertEquals("Player should be 0", 0, move.player());
        assertEquals("From row should be 1", 1, move.fromRow);
        assertEquals("From col should be 1", 1, move.fromCol);
        assertEquals("To row should be 2", 2, move.toRow);
        assertEquals("To col should be 2", 2, move.toCol);
    }

    @Test
    public void testPlayer() {
        // Verify that the player is correctly returned.
        assertEquals("Player should be 0", 0, move.player());
    }

    @Test
    public void testFromRow() {
        // Verify the from row position
        assertEquals("From row should be 1", 1, move.fromRow);
    }

    @Test
    public void testFromCol() {
        // Verify the from column position
        assertEquals("From col should be 1", 1, move.fromCol);
    }

    @Test
    public void testToRow() {
        // Verify the to row position
        assertEquals("To row should be 2", 2, move.toRow);
    }

    @Test
    public void testToCol() {
        // Verify the to column position
        assertEquals("To col should be 2", 2, move.toCol);
    }

}
