package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.*;

public class CheckersStateTest {

    private CheckersState initialState;

    @Before
    public void setUp() {
        initialState = new CheckersState();
    }

    @Test
    public void testInitialBoardSetup() {
        int[][] board = initialState.getBoard();
        assertEquals(2, board[0][1]);
        assertEquals(2, board[2][7]);
        assertEquals(1, board[5][0]);
        assertEquals(1, board[7][6]);
        assertEquals(0, board[4][3]); // Middle of the board should be empty
    }

    @Test
    public void testPlayerTurn() {
        assertEquals(0, initialState.player()); // White starts
    }

    @Test
    public void testMovesForWhite() {
        Collection<Move<Checkers>> whiteMoves = initialState.moves(0);
        assertFalse("White should have moves initially", whiteMoves.isEmpty());
    }

    @Test
    public void testMovesForBlack() {
        Collection<Move<Checkers>> blackMoves = initialState.moves(1);
        assertFalse("Black should have moves initially", blackMoves.isEmpty());
    }

    @Test
    public void testIsTerminalFalseInitially() {
        assertFalse("Game should not be terminal at start", initialState.isTerminal());
    }

    @Test
    public void testNextStateSwitchesPlayer() {
        Move<Checkers> move = initialState.moves(0).iterator().next();
        State<Checkers> next = initialState.next(move);
        assertEquals(1, next.player()); // Black's turn
    }

    @Test
    public void testWinnerNoneAtStart() {
        Optional<Integer> winner = initialState.winner();
        assertFalse("There should be no winner at game start", winner.isPresent());
    }

    @Test
    public void testCaptureMove() {
        // Set up a custom board where a capture is available
        int[][] board = new int[8][8];
        board[4][3] = 1; // white
        board[3][4] = 2; // black
        Random rand = new Random(1);
        CheckersState state = new CheckersState(new Checkers(), board, 0, rand);

        Collection<Move<Checkers>> moves = state.moves(0);
        assertEquals("White should have one capturing move", 1, moves.size());

        Move<Checkers> move = moves.iterator().next();
        State<Checkers> newState = state.next(move);
        assertEquals(0, ((CheckersState) newState).getBoard()[3][4]); // black piece captured
    }

    @Test
    public void testWinnerWhenOpponentHasNoPieces() {
        int[][] board = new int[8][8];
        board[0][0] = 2; // Black piece only
        Random rand = new Random();
        CheckersState state = new CheckersState(new Checkers(), board, 0, rand);

        Optional<Integer> winner = state.winner();
        assertTrue(winner.isPresent());
        assertEquals("Black should win if white has no pieces", Integer.valueOf(1), winner.get());
    }
}
