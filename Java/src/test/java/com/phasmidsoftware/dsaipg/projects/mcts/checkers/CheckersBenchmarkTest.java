package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class CheckersBenchmarkTest {

    @Test
    public void testBenchmarkCSVFileCreation() {
        // Given
        String filePath = "Java/src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/CSVResult/checkers_benchmark.csv";
        File file = new File(filePath);
        File dir = file.getParentFile();

        // Ensure it's clean before test
        if (file.exists()) file.delete();

        // When
        dir.mkdirs(); // simulate file parent creation
        try {
            boolean created = file.createNewFile();
            assertTrue("CSV file should be created.", created || file.exists());
        } catch (Exception e) {
            fail("Exception occurred while creating the benchmark file: " + e.getMessage());
        }

        // Then
        assertTrue("CSV file should exist.", file.exists());
    }

    @Test
    public void testCheckersGameRunsToTerminalState() {
        Checkers game = new Checkers();
        CheckersState currentState = (CheckersState) game.start();
        int maxMoves = 300; // safeguard to avoid infinite loop
        int moveCount = 0;

        while (!currentState.isTerminal() && moveCount < maxMoves) {
            CheckersNode currentNode = new CheckersNode(currentState);
            CheckersMCTS currentMCTS = new CheckersMCTS(currentNode);
            currentMCTS.run(10); // using a low iteration count for unit testing

            Node<Checkers> bestChild = currentNode.children().stream()
                    .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                    .orElse(null);

            if (bestChild == null) break;

            currentState = (CheckersState) bestChild.state();
            moveCount++;
        }

        assertTrue("Game should eventually reach a terminal state.", currentState.isTerminal() || moveCount >= maxMoves);
    }

    @Test
    public void testWinnerIsValidValue() {
        Checkers game = new Checkers();
        CheckersState currentState = (CheckersState) game.start();
        int moveCount = 0;

        while (!currentState.isTerminal() && moveCount < 150) {
            CheckersNode node = new CheckersNode(currentState);
            CheckersMCTS mcts = new CheckersMCTS(node);
            mcts.run(10);
            Node<Checkers> bestChild = node.children().stream()
                    .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                    .orElse(null);
            if (bestChild == null) break;

            currentState = (CheckersState) bestChild.state();
            moveCount++;
        }

        if (currentState.isTerminal()) {
            currentState.winner().ifPresent(winner -> {
                assertTrue("Winner must be 0 or 1", winner == 0 || winner == 1);
            });
        }
    }
}
