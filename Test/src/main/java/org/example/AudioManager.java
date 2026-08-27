package org.example;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioManager {
    private MediaPlayer musicPlayer;
    private MediaPlayer clearPlayer;
    private Media moveMedia;
    private Media clearMedia;
    private boolean sfxEnabled = true;
    private double musicVolume = 0.25;
    private double moveVolume = 0.1;
    private double clearVolume = 1.0;

    public AudioManager() {
        String musicFile = getClass()
                .getResource("/tetris.mp3")
                .toExternalForm();
        Media musicMedia = new Media(musicFile);
        musicPlayer = new MediaPlayer(musicMedia);
        musicPlayer.setVolume(musicVolume);
        String moveFile = getClass()
                .getResource("/move.mp3")
                .toExternalForm();
        moveMedia = new Media(moveFile);
        String clearFile = getClass()
                .getResource("/clear.mp3")
                .toExternalForm();
        clearMedia = new Media(clearFile);
        clearPlayer = new MediaPlayer(clearMedia);
        clearPlayer.setVolume(clearVolume);
    }

    public void playMusic() {
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicPlayer.play();
    }

    public void stopMusic() {
        musicPlayer.stop();
    }

    public void setMusicEnabled(boolean enabled) {
        if (enabled) {
            playMusic();
        } else {
            stopMusic();
        }
    }

    public void setSfxEnabled(boolean enabled) {
        sfxEnabled = enabled;
        if (!enabled) {
            clearPlayer.stop();
        }
    }

    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    public void playMoveSound() {
        if (!sfxEnabled) {
            return;
        }
        MediaPlayer player = new MediaPlayer(moveMedia);
        player.setVolume(moveVolume);
        player.setOnEndOfMedia(player::dispose);
        player.play();
    }

    public void playClearSound() {
        if (!sfxEnabled) {
            return;
        }
        clearPlayer.stop();
        clearPlayer.setVolume(clearVolume);
        clearPlayer.seek(Duration.ZERO);
        clearPlayer.play();
    }
}