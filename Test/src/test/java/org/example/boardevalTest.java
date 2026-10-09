package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class boardevalTest {

    private final boardeval evaluator = boardeval.getInstance();

    @Test
    void getInstanceAlwaysReturnsTheSameSharedInstance() {
        assertSame(boardeval.getInstance(), boardeval.getInstance());
    }

    @ParameterizedTest(name = "[{index}] column {1} height = {2}")
    @MethodSource("columnHeightCases")
    void columnHeightCountsFromTheTopmostFilledCellToTheFloor(int[][] board, int column, int expectedHeight) {
        assertEquals(expectedHeight, evaluator.columnHeight(board, column));
    }

    static Stream<Arguments> columnHeightCases() {
        return Stream.of(
                Arguments.of(new int[][]{{0, 0, 0, 0}}, 0, 0),
                Arguments.of(new int[][]{{0, 1, 0, 0}}, 0, 3),
                Arguments.of(new int[][]{{1, 1, 1, 1}}, 0, 4),
                Arguments.of(new int[][]{{0, 0, 0, 1}, {0, 1, 0, 0}}, 1, 3)
        );
    }

    @Test
    void maximumHeightReturnsTheTallestColumn() {
        int[][] board = {{0, 0, 0, 1}, {0, 1, 0, 0}, {1, 0, 0, 0}};
        assertEquals(4, evaluator.maximumHeight(board));
    }

    @ParameterizedTest(name = "[{index}] holes = {1}")
    @MethodSource("holesCases")
    void holesCountsEmptyCellsCoveredByAFilledCellAbove(int[][] board, int expectedHoles) {
        assertEquals(expectedHoles, evaluator.holes(board));
    }

    static Stream<Arguments> holesCases() {
        return Stream.of(
                Arguments.of(new int[][]{{0, 1, 0, 1}}, 1),
                Arguments.of(new int[][]{{0, 0, 1, 1}}, 0),
                Arguments.of(new int[][]{{1, 0, 0, 1}}, 2),
                Arguments.of(new int[][]{{0, 1, 0, 1}, {1, 0, 1, 0}}, 3)
        );
    }

    @Test
    void bumpinessSumsHeightDifferencesBetweenAdjacentColumns() {
        int[][] board = {{1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 1, 1}};
        assertEquals(6, evaluator.bumpiness(board));
    }

    @ParameterizedTest(name = "[{index}] evaluate = {1}")
    @MethodSource("evaluateCases")
    void evaluateScoresTheBoardFromLinesClearedHolesBumpinessAndHeight(int[][] board, int expectedScore) {
        assertEquals(expectedScore, evaluator.evaluate(board));
    }

    static Stream<Arguments> evaluateCases() {
        return Stream.of(
                Arguments.of(new int[][]{{1, 1}, {1, 1}}, 8),
                Arguments.of(new int[][]{{1, 0}, {0, 0}}, -15),
                Arguments.of(new int[][]{{0, 0}, {0, 0}, {0, 0}}, 0),
                Arguments.of(new int[][]{{1, 1}, {1, 0}}, -1)
        );
    }
}
