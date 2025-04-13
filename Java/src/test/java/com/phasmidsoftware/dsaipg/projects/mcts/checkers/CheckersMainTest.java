package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Optional;

import static org.junit.Assert.*;

public class CheckersMainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        // Redirect System.out to capture the console output
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        // Reset System.out after each test
        System.setOut(originalOut);
    }

    @Test
    public void testMainExecutesSuccessfullyWithGameFlow() {
        // Run CheckersMain.main to simulate the game play
        CheckersMain.main(new String[]{});

        // Check if "Game Over!" appears in the output
        assertTrue("Game should end with 'Game Over!' message", outContent.toString().contains("Game Over!"));

        // Check if at least one move is printed
        assertTrue("Output should contain 'Move #' for at least one move", outContent.toString().contains("Move #"));

        // Check if the board printing logic is being triggered (i.e., 'Current board' appears)
        assertTrue("Board should be printed during the game", outContent.toString().contains("Current board:"));
    }

}
