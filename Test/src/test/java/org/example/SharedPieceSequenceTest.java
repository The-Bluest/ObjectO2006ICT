package org.example;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SharedPieceSequenceTest {

    private static final Set<String> VALID_NAMES =
            Set.of("l", "ll", "square", "s", "zig", "t", "line");

    @Test
    void getPieceOnlyReturnsKnownTetrominoNames() {
        SharedPieceSequence sequence = new SharedPieceSequence();

        for (int i = 0; i < 50; i++) {
            assertTrue(VALID_NAMES.contains(sequence.getPiece(i)));
        }
    }

    @Test
    void getPieceIsMemoisedForTheSameIndex() {
        SharedPieceSequence sequence = new SharedPieceSequence();

        String first = sequence.getPiece(3);
        String second = sequence.getPiece(3);

        assertEquals(first, second);
    }

    @Test
    void sequenceIsSharedRegardlessOfRequestOrder() {
        SharedPieceSequence sequence = new SharedPieceSequence();

        // one player peeks ahead before the other catches up to the same index
        String seenAheadAtIndexFour = sequence.getPiece(4);
        sequence.getPiece(0);
        String seenLaterAtIndexFour = sequence.getPiece(4);

        assertEquals(seenAheadAtIndexFour, seenLaterAtIndexFour);
    }
}
