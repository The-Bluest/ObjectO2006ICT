package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreEntryTest {

    @Test
    void rejectsNegativeScores() {
        assertThrows(IllegalArgumentException.class, () -> new HighScoreEntry("Alice", -1));
    }

    @Test
    void blankOrNullNamesFallBackToAnonymous() {
        assertEquals("Anonymous", new HighScoreEntry(null, 5).getPlayerName());
        assertEquals("Anonymous", new HighScoreEntry("   ", 5).getPlayerName());
    }

    @Test
    void namesAreTrimmed() {
        assertEquals("Bob", new HighScoreEntry("  Bob  ", 5).getPlayerName());
    }

    @Test
    void namesLongerThanTwentyCharactersAreTruncated() {
        String longName = "A".repeat(30);

        HighScoreEntry entry = new HighScoreEntry(longName, 5);

        assertEquals("A".repeat(20), entry.getPlayerName());
    }

    @Test
    void entriesWithSameNameAndScoreAreEqual() {
        assertEquals(new HighScoreEntry("Alice", 10), new HighScoreEntry("Alice", 10));
        assertEquals(
                new HighScoreEntry("Alice", 10).hashCode(),
                new HighScoreEntry("Alice", 10).hashCode()
        );
    }

    @Test
    void entriesWithDifferentScoresAreNotEqual() {
        assertNotEquals(new HighScoreEntry("Alice", 10), new HighScoreEntry("Alice", 11));
    }

    @Test
    void toStringIncludesNameAndScore() {
        assertEquals("Alice: 10", new HighScoreEntry("Alice", 10).toString());
    }
}
