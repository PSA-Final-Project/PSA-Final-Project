package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class CheckersBenchmark {

    public static void main(String[] args) {
        int gamesPerSetting = 20;
        int startIterations = 100;
        int maxIterations = 1600;
        String filePath = "Java/src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/CSVResult/checkers_benchmark.csv";
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Iterations,AvgTotalTime(ms),WinsWhite,AvgTimeWhite(ms),WinsBlack,AvgTimeBlack(ms),Draws,AvgTimeDraw(ms)");
            writer.newLine();

            for (int iterations = startIterations; iterations <= maxIterations; iterations *= 2) {
                long totalTime = 0;
                int winsWhite = 0, winsBlack = 0, draws = 0;
                long totalTimeWhite = 0, totalTimeBlack = 0, totalTimeDraw = 0;

                for (int gameNum = 0; gameNum < gamesPerSetting; gameNum++) {
                    long startTime = System.currentTimeMillis();

                    Checkers game = new Checkers();
                    CheckersState currentState = (CheckersState) game.start();

                    while (!currentState.isTerminal()) {
                        CheckersNode currentNode = new CheckersNode(currentState);
                        CheckersMCTS currentMCTS = new CheckersMCTS(currentNode);
                        currentMCTS.run(iterations);

                        Node<Checkers> bestChild = currentNode.children().stream()
                                .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                                .orElseThrow(() -> new IllegalStateException("No moves available"));

                        currentState = (CheckersState) bestChild.state();
                    }

                    long elapsed = System.currentTimeMillis() - startTime;
                    totalTime += elapsed;

                    Optional<Integer> winner = currentState.winner();
                    if (winner.isPresent()) {
                        if (winner.get() == game.opener()) {
                            winsWhite++;
                            totalTimeWhite += elapsed;
                        } else {
                            winsBlack++;
                            totalTimeBlack += elapsed;
                        }
                    } else {
                        draws++;
                        totalTimeDraw += elapsed;
                    }
                }

                int totalGames = winsWhite + winsBlack + draws;
                long avgTotalTime = totalGames > 0 ? totalTime / totalGames : 0;
                double avgTimeWhite = winsWhite > 0 ? (double) totalTimeWhite / winsWhite : 0;
                double avgTimeBlack = winsBlack > 0 ? (double) totalTimeBlack / winsBlack : 0;
                double avgTimeDraw = draws > 0 ? (double) totalTimeDraw / draws : 0;

                String result = iterations + "," + avgTotalTime + "," +
                        winsWhite + "," + avgTimeWhite + "," +
                        winsBlack + "," + avgTimeBlack + "," +
                        draws + "," + avgTimeDraw;

                writer.write(result);
                writer.newLine();

                System.out.println("Iterations: " + iterations +
                        " | Avg Total Time: " + avgTotalTime + " ms | WinsWhite: " + winsWhite + " (Avg: " + avgTimeWhite + " ms)" +
                        " | WinsBlack: " + winsBlack + " (Avg: " + avgTimeBlack + " ms)" +
                        " | Draws: " + draws + " (Avg: " + avgTimeDraw + " ms)");
            }

        } catch (IOException e) {
            System.err.println("Error writing benchmark file: " + e.getMessage());
        }
    }
}
