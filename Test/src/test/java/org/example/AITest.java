package org.example;

import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AITest {

    private final AI ai = new AI(boardeval.getInstance());

    @Test
    void bestMovePicksTheLineClearingPlacementOnAnEmptyBoard() {
        int[][] board = new int[4][4];
        form piece = controller.makeShape("line", 4 * controller.size);

        AI.Move move = ai.bestMove(piece, board);

        assertNotNull(move);
        assertEquals(0, move.rotation());
        assertEquals(0, move.column());
        assertEquals(3, move.row());
        assertEquals(4, move.score());
    }

    @Test
    void bestMoveReturnsNullWhenTheBoardIsAlreadyFull() {
        int[][] board = new int[4][4];
        for (int[] column : board) {
            Arrays.fill(column, 1);
        }
        form piece = controller.makeShape("square", 4 * controller.size);

        assertNull(ai.bestMove(piece, board));
    }

    @Test
    void bestMoveReturnsNullForAnUnrecognisedPiece() {
        form piece = new form(
                new Rectangle(1, 1), new Rectangle(1, 1),
                new Rectangle(1, 1), new Rectangle(1, 1)
        );

        assertNull(ai.bestMove(piece, new int[4][4]));
    }

    @Test
    void applyMoveHardDropsEachCornerToTheComputedCellAndAdvancesFormState() {
        form piece = controller.makeShape("square", 4 * controller.size);
        AI.Move move = new AI.Move(0, 2, 1, 99);

        ai.applyMove(piece, move);

        int[][] cells = AI.shapeStates("square")[0];
        assertEquals((move.column() + cells[0][0]) * controller.size, piece.a.getX());
        assertEquals((move.row() + cells[0][1]) * controller.size, piece.a.getY());
        assertEquals((move.column() + cells[3][0]) * controller.size, piece.d.getX());
        assertEquals((move.row() + cells[3][1]) * controller.size, piece.d.getY());
        assertEquals(1, piece.form);
    }

    @Test
    void playAppliesWhicheverMoveBestMoveComputed() {
        int[][] board = new int[4][4];
        form piece = controller.makeShape("line", 4 * controller.size);

        AI.Move move = ai.play(piece, board);

        assertNotNull(move);
        assertEquals(move.row() * controller.size, piece.a.getY());
    }

    @Test
    void shapeStatesKeepTheSameCellsForEveryRotationOfASquare() {
        int[][][] states = AI.shapeStates("square");
        Set<String> spawnCells = toCellSet(states[0]);

        for (int[][] state : states) {
            assertEquals(spawnCells, toCellSet(state));
        }
    }

    @Test
    void shapeStatesRotateALineBetweenHorizontalAndVertical() {
        int[][][] states = AI.shapeStates("line");

        assertEquals(3, maxCol(states[0]));
        assertEquals(0, maxRow(states[0]));
        assertEquals(0, maxCol(states[1]));
        assertEquals(3, maxRow(states[1]));
    }

    private static Set<String> toCellSet(int[][] cells) {
        Set<String> set = new HashSet<>();
        for (int[] cell : cells) {
            set.add(cell[0] + "," + cell[1]);
        }
        return set;
    }

    private static int maxCol(int[][] cells) {
        int max = 0;
        for (int[] cell : cells) max = Math.max(max, cell[0]);
        return max;
    }

    private static int maxRow(int[][] cells) {
        int max = 0;
        for (int[] cell : cells) max = Math.max(max, cell[1]);
        return max;
    }
}