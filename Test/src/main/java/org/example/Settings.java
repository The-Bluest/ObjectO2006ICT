package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;

/**
 * User-configurable game settings persisted as JSON.
 */
public class Settings {
    public static final int MIN_HEIGHT = 8;
    public static final int MAX_HEIGHT = 24;
    public static final int MIN_WIDTH = 4;
    public static final int MAX_WIDTH = 12;
    public static final double MIN_SPEED = 1;
    public static final double MAX_SPEED = 10;
    public static final int MIN_DIFFICULTY = 1;
    public static final int MAX_DIFFICULTY = 5;

    private static final Path DEFAULT_FILE = Path.of("settings.json");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final JsonFileStore<Settings> STORE =
            new JsonFileStore<>(GSON, Settings.class);

    private int gameHeight = 16;
    private int gameWidth = 8;
    private double gameSpeed = 5;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    private int difficulty = 1;
    private PlayerType playerMode = PlayerType.HUMAN;

    private transient Path file = DEFAULT_FILE;

    public Settings() {
    }

    private Settings(Path file) {
        this.file = file;
    }

    public static Settings load() {
        return load(DEFAULT_FILE);
    }

    public static Settings load(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Settings file cannot be null");
        }

        Settings loaded = STORE.load(file, new Settings(file));
        loaded.file = file;
        loaded.normalise();
        return loaded;
    }

    public void save() {
        save(file == null ? DEFAULT_FILE : file);
    }

    public void save(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Settings file cannot be null");
        }

        normalise();
        STORE.save(file, this);
        this.file = file;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    public void setGameHeight(int gameHeight) {
        this.gameHeight = clamp(gameHeight, MIN_HEIGHT, MAX_HEIGHT);
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public void setGameWidth(int gameWidth) {
        this.gameWidth = clamp(gameWidth, MIN_WIDTH, MAX_WIDTH);
    }

    public double getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(double gameSpeed) {
        if (!Double.isFinite(gameSpeed)) {
            return;
        }
        this.gameSpeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, gameSpeed));
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = clamp(
                difficulty,
                MIN_DIFFICULTY,
                MAX_DIFFICULTY
        );
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    public void setSfxEnabled(boolean sfxEnabled) {
        this.sfxEnabled = sfxEnabled;
    }

    public boolean isAiEnabled() {
        return playerMode == PlayerType.AI;
    }

    public boolean isExternalPlayerEnabled() {
        return playerMode == PlayerType.EXTERNAL;
    }

    public PlayerType getPlayerMode() {
        return playerMode;
    }

    public void setPlayerMode(PlayerType playerMode) {
        this.playerMode = playerMode == null ? PlayerType.HUMAN : playerMode;
    }

    private void normalise() {
        gameHeight = clamp(gameHeight, MIN_HEIGHT, MAX_HEIGHT);
        gameWidth = clamp(gameWidth, MIN_WIDTH, MAX_WIDTH);
        if (!Double.isFinite(gameSpeed)) {
            gameSpeed = 5;
        } else {
            gameSpeed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, gameSpeed));
        }
        difficulty = clamp(difficulty, MIN_DIFFICULTY, MAX_DIFFICULTY);
        if (playerMode == null) {
            playerMode = PlayerType.HUMAN;
        }
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}