package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class CheckersBenchmarkTest {

    private final String testFilePath = "Java/src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/CSVResult/checkers_benchmark.csv";
    private final File testFile = new File(testFilePath);

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        // Delete old file if it exists
        if (testFile.exists()) {
            boolean deleted = testFile.delete();
            if (!deleted) {
                System.err.println("Warning: Old test file could not be deleted.");
            }
        }

        // Redirect System.out
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        // Reset System.out
        System.setOut(originalOut);

        // Optionally clean up created file
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    public void testMainExecutesSuccessfully() {
        // Run the benchmark
        CheckersBenchmark.main(new String[]{});

        // Validate output file exists
        assertTrue("CSV file should be created.", testFile.exists());

        // Validate that CSV has expected header
        try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
            String header = reader.readLine();
            assertEquals("Iterations,AvgTotalTime(ms),WinsWhite,AvgTimeWhite(ms),WinsBlack,AvgTimeBlack(ms),Draws,AvgTimeDraw(ms)", header);
        } catch (IOException e) {
            fail("Failed to read CSV file: " + e.getMessage());
        }

        // Check if console output contains expected words
        String consoleOutput = outContent.toString();
        assertTrue("Console output should contain 'Iterations:'", consoleOutput.contains("Iterations:"));
    }
}
