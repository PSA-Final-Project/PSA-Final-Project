package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Game;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

public class Checkers implements Game<Checkers> {

    public State<Checkers> start() {
        return new CheckersState();
    }

    public int opener() {
        return 0; // 0: white, 1: black
    }
}
