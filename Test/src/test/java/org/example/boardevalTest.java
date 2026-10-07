package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class boardevalTest {

    private final boardeval evaluator = boardeval.getInstance();

    @Test
    void getInstanceAlwaysReturnsTheSameSharedInstance() {
        assertSame(boardeval.getInstance(), boardeval.getInstance());
    }

    @Test
    void columnHeightIsZeroForAnEmptyColumn() {
        int[][] board = {{0, 0, 0, 0}};
        assertEquals(0, evaluator.columnHeight(board, 0));
    }

    @Test
    void columnHeightCountsFromTheTopmostFilledCellToTheFloor() {
        int[][] board = {{0, 1, 0, 0}};
        assertEquals(3, evaluator.columnHeight(board, 0));
    }

    @Test
    void maximumHeightReturnsTheTallestColumn() {
        int[][] board = {{0, 0, 0, 1}, {0, 1, 0, 0}, {1, 0, 0, 0}};
        assertEquals(4, evaluator.maximumHeight(board));
    }

    @Test
    void holesCountsEmptyCellsCoveredByAFilledCellAbove() {
        int[][] board = {{0, 1, 0, 1}};
        assertEquals(1, evaluator.holes(board));
    }

    @Test
    void holesIgnoresEmptyCellsAboveTheFirstFilledCell() {
        int[][] board = {{0, 0, 1, 1}};
        assertEquals(0, evaluator.holes(board));
    }

    @Test
    void bumpinessSumsHeightDifferencesBetweenAdjacentColumns() {
        int[][] board = {{1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 1, 1}};
        assertEquals(6, evaluator.bumpiness(board));
    }

    @Test
    void evaluateRewardsClearedLinesAndPenalisesRemainingHeight() {
        int[][] board = {{1, 1}, {1, 1}};
        assertEquals(8, evaluator.evaluate(board));
    }

    @Test
    void evaluatePenalisesHolesBumpinessAndHeightWhenNoLineClears() {
        int[][] board = {{1, 0}, {0, 0}};
        assertEquals(-15, evaluator.evaluate(board));
    }
}
