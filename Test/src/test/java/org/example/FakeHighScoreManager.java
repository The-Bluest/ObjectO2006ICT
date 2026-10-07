package org.example;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Fake: a working, in-memory stand-in for HighScoreManager that never touches the filesystem,
// but still sorts/caps entries the same way the real implementation does.
class FakeHighScoreManager extends HighScoreManager {
    private List<HighScoreEntry> stored = new ArrayList<>();

    FakeHighScoreManager() {
        super(Path.of("unused-by-fake-high-score-manager.json"));
    }

    @Override
    public List<HighScoreEntry> loadScores() {
        return List.copyOf(stored);
    }

    @Override
    public void updateHighScore(String playerName, int score) {
        List<HighScoreEntry> scores = new ArrayList<>(stored);
        scores.add(new HighScoreEntry(playerName, score));
        saveScores(scores);
    }

    @Override
    public void saveScores(Collection<HighScoreEntry> scores) {
        if (scores == null) {
            throw new IllegalArgumentException("Scores cannot be null");
        }

        stored = scores.stream()
                .filter(Objects::nonNull)
                .sorted(
                        Comparator.comparingInt(HighScoreEntry::getScore)
                                .reversed()
                                .thenComparing(
                                        HighScoreEntry::getPlayerName,
                                        String.CASE_INSENSITIVE_ORDER
                                )
                )
                .limit(MAX_SCORES)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
