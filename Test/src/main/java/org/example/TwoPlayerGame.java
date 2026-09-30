package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TwoPlayerGame {
    private final Settings settings;
    private PlayerBoard player1;
    private PlayerBoard player2;
    private Timeline gameLoop;
    private final SharedPieceSequence sharedSequence;
    public TwoPlayerGame(Settings settings) {
        this.settings = settings;
        this.sharedSequence =
                new SharedPieceSequence();
    }

    public void start(
            StackPane root,
            PlayerType player1Type,
            PlayerType player2Type,
            Runnable onBackToMenu
    ) {
        player1 =
                new PlayerBoard(
                        "Player 1",
                        player1Type,
                        sharedSequence,
                        settings
                );
        player2 =
                new PlayerBoard(
                        "Player 2",
                        player2Type,
                        sharedSequence,
                        settings
                );
        Label title =
                new Label("Two Player Mode");
        title.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );
        Label modeLabel =
                new Label(
                        player1Type
                                + " vs "
                                + player2Type
                );
        modeLabel.setStyle(
                "-fx-font-size: 18px;"
        );
        HBox boards =
                new HBox(
                        30,
                        player1.getView(),
                        player2.getView()
                );
        boards.setAlignment(Pos.CENTER);
        Button backButton =
                new Button("Back to Menu");
        backButton.setOnAction(event -> {
            stop();
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });
        VBox layout =
                new VBox(
                        10,
                        title,
                        modeLabel,
                        boards,
                        backButton
                );
        layout.setPadding(
                new Insets(15)
        );
        layout.setAlignment(
                Pos.TOP_CENTER
        );
        root.getChildren().setAll(layout);
        double boardWidth =
                settings.getGameWidth()
                        * Tetris.size;
        double boardHeight =
                settings.getGameHeight()
                        * Tetris.size;
        root.getScene()
                .getWindow()
                .setWidth(
                        boardWidth * 2 + 120
                );
        root.getScene()
                .getWindow()
                .setHeight(
                        boardHeight + 180
                );
        startGameLoop();
    }

    private void startGameLoop() {
        final int tickMs = 30;
        gameLoop =
                new Timeline(
                        new KeyFrame(
                                Duration.millis(tickMs),
                                event -> {
                                    if (!player1.isGameOver()) {
                                        player1.tick(
                                                tickMs
                                        );
                                    }
                                    if (!player2.isGameOver()) {
                                        player2.tick(
                                                tickMs
                                        );
                                    }
                                    if (
                                            player1.isGameOver()
                                                    &&
                                                    player2.isGameOver()
                                    ) {
                                        gameLoop.stop();
                                    }
                                }
                        )
                );
        gameLoop.setCycleCount(
                Timeline.INDEFINITE
        );
        gameLoop.play();
    }

    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public PlayerBoard getPlayer1() {
        return player1;
    }

    public PlayerBoard getPlayer2() {
        return player2;
    }
}