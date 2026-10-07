package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void loadScoresOnAMissingFileReturnsAnEmptyList() {
        HighScoreManager manager = new HighScoreManager(tempDir.resolve("scores.json"));

        assertEquals(List.of(), manager.loadScores());
    }

    @Test
    void updateHighScorePersistsAcrossInstances() {
        Path file = tempDir.resolve("scores.json");
        new HighScoreManager(file).updateHighScore("Alice", 100);

        List<HighScoreEntry> reloaded = new HighScoreManager(file).loadScores();

        assertEquals(1, reloaded.size());
        assertEquals("Alice", reloaded.get(0).getPlayerName());
        assertEquals(100, reloaded.get(0).getScore());
    }

    @Test
    void scoresAreSortedHighestFirst() {
        HighScoreManager manager = new HighScoreManager(tempDir.resolve("scores.json"));
        manager.updateHighScore("Low", 10);
        manager.updateHighScore("High", 90);
        manager.updateHighScore("Mid", 50);

        List<HighScoreEntry> scores = manager.loadScores();

        assertEquals(
                List.of("High", "Mid", "Low"),
                scores.stream().map(HighScoreEntry::getPlayerName).toList()
        );
    }

    @Test
    void tiesAreBrokenAlphabeticallyIgnoringCase() {
        HighScoreManager manager = new HighScoreManager(tempDir.resolve("scores.json"));
        manager.updateHighScore("bob", 50);
        manager.updateHighScore("Alice", 50);

        List<HighScoreEntry> scores = manager.loadScores();

        assertEquals(
                List.of("Alice", "bob"),
                scores.stream().map(HighScoreEntry::getPlayerName).toList()
        );
    }

    @Test
    void onlyTheTopTenScoresAreKept() {
        HighScoreManager manager = new HighScoreManager(tempDir.resolve("scores.json"));
        for (int i = 0; i < 15; i++) {
            manager.updateHighScore("Player" + i, i);
        }

        List<HighScoreEntry> scores = manager.loadScores();

        assertEquals(HighScoreManager.MAX_SCORES, scores.size());
        assertEquals(14, scores.get(0).getScore());
        assertEquals(5, scores.get(scores.size() - 1).getScore());
    }

    @Test
    void constructorRejectsNullFile() {
        assertThrows(IllegalArgumentException.class, () -> new HighScoreManager((Path) null));
    }

    @Test
    void saveScoresRejectsNullCollection() {
        HighScoreManager manager = new HighScoreManager(tempDir.resolve("scores.json"));

        assertThrows(IllegalArgumentException.class, () -> manager.saveScores(null));
    }
}
