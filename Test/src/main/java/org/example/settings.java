package org.example;


public class settings extends Settings{

    private int gameHeight;
    private int gameWidth;
    private double gameSpeed;
    private boolean musicEnabled;
    private boolean sfxEnabled;
    private int difficulty;
    private boolean aiPlay;

    public settings() {
        super();  
        gameHeight = 16;
        gameWidth = 8;
        gameSpeed = 5;
        musicEnabled = true;
        sfxEnabled = true;
        difficulty = 1;
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

    public int getDifficulty(){
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
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