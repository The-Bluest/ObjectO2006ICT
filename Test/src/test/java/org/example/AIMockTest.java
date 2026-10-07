package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Mock (Mockito): unlike the hand-rolled Stub/Fake/Spy elsewhere, this stubs return values AND
// verifies interactions on the real boardeval type via bytecode, without subclassing it.
class AIMockTest {

    @Test
    void bestMoveDelegatesScoringToTheInjectedEvaluator() {
        boardeval mockEvaluator = mock(boardeval.class);
        when(mockEvaluator.evaluate(any())).thenReturn(0);
        AI ai = new AI(mockEvaluator);
        form piece = controller.makeShape("square", 4 * controller.size);

        ai.bestMove(piece, new int[4][4]);

        verify(mockEvaluator, atLeastOnce()).evaluate(any());
    }

    @Test
    void bestMovePicksWhicheverCandidateTheMockScoresHighest() {
        boardeval mockEvaluator = mock(boardeval.class);
        AI ai = new AI(mockEvaluator);
        int[][] board = new int[4][4];
        form piece = controller.makeShape("line", 4 * controller.size);

        when(mockEvaluator.evaluate(any())).thenReturn(0);
        // only the vertical placement landing in column 2 gets a winning score
        when(mockEvaluator.evaluate(
                argThat(candidate -> candidate[2][3] == 1 && candidate[0][3] == 0)
        )).thenReturn(100);

        AI.Move move = ai.bestMove(piece, board);

        assertNotNull(move);
        assertEquals(1, move.rotation());
        assertEquals(2, move.column());
        assertEquals(100, move.score());
        verify(mockEvaluator, times(10)).evaluate(any());
    }
}
