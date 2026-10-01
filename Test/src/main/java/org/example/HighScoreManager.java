package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Loads and persists the ten highest scores as JSON.
 */
public class HighScoreManager {
    public static final int MAX_SCORES = 10;

    private static final Path DEFAULT_FILE = Path.of("highscores.json");
    private static final Path LEGACY_FILE = Path.of("thing.txt");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final Type SCORE_LIST_TYPE =
            new TypeToken<List<HighScoreEntry>>() { }.getType();

    private static final Comparator<HighScoreEntry> LEADERBOARD_ORDER =
            Comparator.comparingInt(HighScoreEntry::getScore)
                    .reversed()
                    .thenComparing(
                            HighScoreEntry::getPlayerName,
                            String.CASE_INSENSITIVE_ORDER
                    );

    private final Path file;

    public HighScoreManager() {
        this(DEFAULT_FILE);
    }

    /**
     * A separate path makes persistence deterministic and easy to test.
     */
    public HighScoreManager(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("High score file cannot be null");
        }
        this.file = file;
    }

    public List<HighScoreEntry> loadScores() {
        if (!Files.exists(file)) {
            return loadLegacyScoresIfPresent();
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            List<HighScoreEntry> loadedScores =
                    GSON.fromJson(reader, SCORE_LIST_TYPE);
            if (loadedScores == null) {
                return List.of();
            }
            return prepareForStorage(loadedScores);
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            System.err.println("Could not load high scores from " + file + ": "
                    + exception.getMessage());
            return List.of();
        }
    }

    public void updateHighScore(String playerName, int score) {
        List<HighScoreEntry> scores = new ArrayList<>(loadScores());
        scores.add(new HighScoreEntry(playerName, score));
        saveScores(scores);
    }

    public void saveScores(Collection<HighScoreEntry> scores) {
        if (scores == null) {
            throw new IllegalArgumentException("Scores cannot be null");
        }

        List<HighScoreEntry> scoresToSave = prepareForStorage(scores);
        Path absoluteFile = file.toAbsolutePath();
        Path parent = absoluteFile.getParent();
        Path temporaryFile = absoluteFile.resolveSibling(
                absoluteFile.getFileName() + ".tmp");

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(temporaryFile)) {
                GSON.toJson(scoresToSave, SCORE_LIST_TYPE, writer);
            }

            moveIntoPlace(temporaryFile, absoluteFile);
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw new IllegalStateException(
                    "Could not save high scores to " + file,
                    exception
            );
        }
    }

    private List<HighScoreEntry> loadLegacyScoresIfPresent() {
        if (file.equals(DEFAULT_FILE) && Files.exists(LEGACY_FILE)) {
            try (BufferedReader reader = Files.newBufferedReader(LEGACY_FILE)) {
                List<HighScoreEntry> migratedScores = reader.lines()
                        .map(line -> parseLegacyScore(line.trim()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                return prepareForStorage(migratedScores);
            } catch (IOException exception) {
                System.err.println("Could not load legacy high scores from "
                        + LEGACY_FILE + ": " + exception.getMessage());
            }
        }
        return List.of();
    }

    private static HighScoreEntry parseLegacyScore(String line) {
        try {
            return new HighScoreEntry("Anonymous", Integer.parseInt(line));
        } catch (IllegalArgumentException ignored) {
            // Ignore malformed legacy rows rather than breaking the menu.
            return null;
        }
    }

    private static List<HighScoreEntry> prepareForStorage(
            Collection<HighScoreEntry> scores) {
        if (scores == null) {
            throw new IllegalArgumentException("Scores cannot be null");
        }

        return scores.stream()
                .filter(Objects::nonNull)
                .map(HighScoreManager::resanitise)
                .filter(Objects::nonNull)
                .sorted(LEADERBOARD_ORDER)
                .limit(MAX_SCORES)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static HighScoreEntry resanitise(HighScoreEntry score) {
        try {
            return new HighScoreEntry(score.getPlayerName(), score.getScore());
        } catch (IllegalArgumentException ignored) {
            // Ignore invalid rows loaded from a manually edited JSON file.
            return null;
        }
    }

    private static void moveIntoPlace(Path temporaryFile, Path targetFile)
            throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    targetFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    temporaryFile,
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}