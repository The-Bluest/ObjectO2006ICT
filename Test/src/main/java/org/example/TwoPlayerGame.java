package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TwoPlayerGame {
    private final Settings settings;
    private PlayerBoard player1;
    private PlayerBoard player2;
    private Timeline gameLoop;
    private final SharedPieceSequence sharedSequence;
    private boolean paused = false;
    private Label pausedLabel;
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
        boards.setFocusTraversable(true);
        paused = false;
        pausedLabel = new Label("PAUSED");
        pausedLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: red;"
        );
        pausedLabel.setVisible(false);
        Button pauseButton =
                new Button("Pause");
        pauseButton.setFocusTraversable(false);
        pauseButton.setOnAction(event -> {
            togglePaused(pauseButton);
        });
        Button quitButton =
                new Button("Quit");
        quitButton.setFocusTraversable(false);
        quitButton.setOnAction(event -> {
            stop();
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });
        Button backButton =
                new Button("Back to Menu");
        backButton.setFocusTraversable(false);
        backButton.setOnAction(event -> {
            stop();
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });
        HBox buttonRow =
                new HBox(
                        10,
                        pauseButton,
                        quitButton,
                        backButton
                );
        buttonRow.setAlignment(Pos.CENTER);
        VBox layout =
                new VBox(
                        10,
                        title,
                        modeLabel,
                        pausedLabel,
                        boards,
                        buttonRow
                );
        layout.setPadding(
                new Insets(15)
        );
        layout.setAlignment(
                Pos.TOP_CENTER
        );
        root.getChildren().setAll(new Group(layout));
        // shrink the whole layout to fit the screen (no scale-up) instead of scrolling or clipping off-screen
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double desiredWidth = layout.prefWidth(-1);
            double desiredHeight = layout.prefHeight(-1);
            double maxWidth = screenBounds.getWidth() - 20;
            double maxHeight = screenBounds.getHeight() - 20;
            double scale = Math.min(
                    1.0,
                    Math.min(
                            maxWidth / desiredWidth,
                            maxHeight / desiredHeight
                    )
            );
            layout.setScaleX(scale);
            layout.setScaleY(scale);
            stage.setWidth(desiredWidth * scale + 20);
            stage.setHeight(desiredHeight * scale + 20);
            stage.centerOnScreen();
        });
        startGameLoop();
        setupControls(root);
        // arrow keys default to JavaFX focus traversal unless something neutral owns focus first
        Platform.runLater(boards::requestFocus);
    }

    // Player 1 = WASD, Player 2 = Arrow keys
    private void setupControls(StackPane root) {
        root.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.P) {
                togglePaused(null);
                return;
            }
            if (paused) {
                return;
            }
            switch (event.getCode()) {
                case A -> player1.handleInput(PlayerBoard.Action.LEFT);
                case D -> player1.handleInput(PlayerBoard.Action.RIGHT);
                case S -> player1.handleInput(PlayerBoard.Action.DOWN);
                case W -> player1.handleInput(PlayerBoard.Action.ROTATE);
                case LEFT -> player2.handleInput(PlayerBoard.Action.LEFT);
                case RIGHT -> player2.handleInput(PlayerBoard.Action.RIGHT);
                case DOWN -> player2.handleInput(PlayerBoard.Action.DOWN);
                case UP -> player2.handleInput(PlayerBoard.Action.ROTATE);
                default -> {
                }
            }
        });
    }

    private void togglePaused(Button pauseButton) {
        paused = !paused;
        pausedLabel.setVisible(paused);
        if (pauseButton != null) {
            pauseButton.setText(paused ? "Resume" : "Pause");
        }
    }

    private void startGameLoop() {
        final int tickMs = 30;
        gameLoop =
                new Timeline(
                        new KeyFrame(
                                Duration.millis(tickMs),
                                event -> {
                                    if (paused) {
                                        return;
                                    }
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