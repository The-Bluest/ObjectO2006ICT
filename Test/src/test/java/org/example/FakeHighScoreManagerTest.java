package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FakeHighScoreManagerTest {

    @Test
    void behavesLikeARealHighScoreManagerWithoutTouchingDisk(@TempDir Path tempDir) {
        FakeHighScoreManager fake = new FakeHighScoreManager();
        HighScoreManager real = new HighScoreManager(tempDir.resolve("scores.json"));

        for (HighScoreManager manager : List.of(fake, real)) {
            manager.updateHighScore("bob", 50);
            manager.updateHighScore("Alice", 50);
            manager.updateHighScore("Highest", 999);
        }

        List<String> fakeNames = fake.loadScores().stream().map(HighScoreEntry::getPlayerName).toList();
        List<String> realNames = real.loadScores().stream().map(HighScoreEntry::getPlayerName).toList();

        assertEquals(realNames, fakeNames);
        assertEquals(List.of("Highest", "Alice", "bob"), fakeNames);
    }

    @Test
    void isIsolatedPerInstanceAndNeverTouchesTheFileSystem() {
        FakeHighScoreManager first = new FakeHighScoreManager();
        FakeHighScoreManager second = new FakeHighScoreManager();

        first.updateHighScore("Alice", 10);

        assertTrue(second.loadScores().isEmpty());
    }

    @Test
    void stillEnforcesTheTopTenCap() {
        FakeHighScoreManager fake = new FakeHighScoreManager();
        for (int i = 0; i < 15; i++) {
            fake.updateHighScore("Player" + i, i);
        }

        assertEquals(HighScoreManager.MAX_SCORES, fake.loadScores().size());
    }
}
