package org.example;

public class settings {

    private int gameHeight;
    private int gameWidth;
    private double gameSpeed;
    private boolean musicEnabled;
    private boolean sfxEnabled;
    private boolean aiPlay;

    public settings() {
        gameHeight = 16;
        gameWidth = 8;
        gameSpeed = 5;
        musicEnabled = true;
        sfxEnabled = true;
        aiPlay= false;

    }

    public int getGameHeight() {
        return gameHeight;
    }

    public void setGameHeight(int gameHeight) {
        this.gameHeight = gameHeight;
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public void setGameWidth(int gameWidth) {
        this.gameWidth = gameWidth;
    }

    public double getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(double gameSpeed) {
        this.gameSpeed = gameSpeed;
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

    public boolean isAiEnabled(){return aiPlay;}

    public void setAiPlay(boolean aiEnabled){this.aiPlay=aiEnabled;}
}