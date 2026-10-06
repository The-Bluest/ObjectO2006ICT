package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {

    @TempDir
    Path tempDir;

    @Test
    void gameHeightIsClampedToValidRange() {
        Settings settings = new Settings();

        settings.setGameHeight(Settings.MIN_HEIGHT - 5);
        assertEquals(Settings.MIN_HEIGHT, settings.getGameHeight());

        settings.setGameHeight(Settings.MAX_HEIGHT + 5);
        assertEquals(Settings.MAX_HEIGHT, settings.getGameHeight());
    }

    @Test
    void gameWidthIsClampedToValidRange() {
        Settings settings = new Settings();

        settings.setGameWidth(Settings.MIN_WIDTH - 1);
        assertEquals(Settings.MIN_WIDTH, settings.getGameWidth());

        settings.setGameWidth(Settings.MAX_WIDTH + 1);
        assertEquals(Settings.MAX_WIDTH, settings.getGameWidth());
    }

    @Test
    void gameSpeedIsClampedAndIgnoresNonFiniteValues() {
        Settings settings = new Settings();

        settings.setGameSpeed(Settings.MIN_SPEED - 1);
        assertEquals(Settings.MIN_SPEED, settings.getGameSpeed());

        settings.setGameSpeed(Settings.MAX_SPEED + 1);
        assertEquals(Settings.MAX_SPEED, settings.getGameSpeed());

        settings.setGameSpeed(7);
        settings.setGameSpeed(Double.NaN);
        assertEquals(7, settings.getGameSpeed());
    }

    @Test
    void difficultyIsClampedToValidRange() {
        Settings settings = new Settings();

        settings.setDifficulty(Settings.MIN_DIFFICULTY - 1);
        assertEquals(Settings.MIN_DIFFICULTY, settings.getDifficulty());

        settings.setDifficulty(Settings.MAX_DIFFICULTY + 1);
        assertEquals(Settings.MAX_DIFFICULTY, settings.getDifficulty());
    }

    @Test
    void nullPlayerModeFallsBackToHuman() {
        Settings settings = new Settings();

        settings.setPlayerMode(null);

        assertEquals(PlayerType.HUMAN, settings.getPlayerMode());
        assertFalse(settings.isAiEnabled());
        assertFalse(settings.isExternalPlayerEnabled());
    }

    @Test
    void aiAndExternalFlagsMatchThePlayerMode() {
        Settings settings = new Settings();

        settings.setPlayerMode(PlayerType.AI);
        assertTrue(settings.isAiEnabled());

        settings.setPlayerMode(PlayerType.EXTERNAL);
        assertTrue(settings.isExternalPlayerEnabled());
    }

    @Test
    void saveThenLoadRoundTripsAllFields() {
        Path file = tempDir.resolve("settings.json");
        Settings original = new Settings();
        original.setGameHeight(20);
        original.setGameWidth(10);
        original.setGameSpeed(8);
        original.setDifficulty(3);
        original.setMusicEnabled(false);
        original.setSfxEnabled(false);
        original.setPlayerMode(PlayerType.AI);
        original.save(file);

        Settings loaded = Settings.load(file);

        assertEquals(original.getGameHeight(), loaded.getGameHeight());
        assertEquals(original.getGameWidth(), loaded.getGameWidth());
        assertEquals(original.getGameSpeed(), loaded.getGameSpeed());
        assertEquals(original.getDifficulty(), loaded.getDifficulty());
        assertEquals(original.isMusicEnabled(), loaded.isMusicEnabled());
        assertEquals(original.isSfxEnabled(), loaded.isSfxEnabled());
        assertEquals(original.getPlayerMode(), loaded.getPlayerMode());
    }

    @Test
    void loadOnAMissingFileReturnsNormalisedDefaults() {
        Settings loaded = Settings.load(tempDir.resolve("missing.json"));

        assertEquals(16, loaded.getGameHeight());
        assertEquals(8, loaded.getGameWidth());
        assertEquals(PlayerType.HUMAN, loaded.getPlayerMode());
    }

    @Test
    void loadRejectsNullFile() {
        assertThrows(IllegalArgumentException.class, () -> Settings.load(null));
    }

    @Test
    void saveRejectsNullFile() {
        assertThrows(IllegalArgumentException.class, () -> new Settings().save((Path) null));
    }
}
