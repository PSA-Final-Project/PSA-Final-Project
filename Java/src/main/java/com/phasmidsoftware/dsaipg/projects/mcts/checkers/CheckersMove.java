package com.phasmidsoftware.dsaipg.projects.mcts.checkers;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;


public class CheckersMove implements Move<Checkers> {

    final int player;
    final int fromRow, fromCol, toRow, toCol;

    public CheckersMove(int player, int fromRow, int fromCol, int toRow, int toCol) {
        this.player = player;
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public int player() {
        return player;
    }
}