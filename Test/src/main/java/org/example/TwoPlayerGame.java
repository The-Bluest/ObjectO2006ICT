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

    //private boolean paused = false;
    private GameState gameState;                  // advanced pattern 2: state pattern
    private Label pausedLabel;

    private GameCommand player1LeftCommand;       // advanced pattern 1:command pattern
    private GameCommand player1RightCommand;
    private GameCommand player1DownCommand;
    private GameCommand player1RotateCommand;
    private GameCommand player2LeftCommand;
    private GameCommand player2RightCommand;
    private GameCommand player2DownCommand;
    private GameCommand player2RotateCommand;

    public TwoPlayerGame(Settings settings) {
        this.settings = settings;
        this.sharedSequence =
                new SharedPieceSequence();
        this.gameState = new RunningState();
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

        player1LeftCommand = new PlayerMoveCommand(player1, PlayerBoard.Action.LEFT);
        player1RightCommand = new PlayerMoveCommand(player1, PlayerBoard.Action.RIGHT);
        player1DownCommand = new PlayerMoveCommand(player1, PlayerBoard.Action.DOWN);
        player1RotateCommand = new PlayerMoveCommand(player1, PlayerBoard.Action.ROTATE);

        player2LeftCommand = new PlayerMoveCommand(player2, PlayerBoard.Action.LEFT);
        player2RightCommand = new PlayerMoveCommand(player2, PlayerBoard.Action.RIGHT);
        player2DownCommand = new PlayerMoveCommand(player2, PlayerBoard.Action.DOWN);
        player2RotateCommand = new PlayerMoveCommand(player2, PlayerBoard.Action.ROTATE);

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
        // paused = false;
        gameState = new RunningState();
        pausedLabel = new Label("PAUSED");
        pausedLabel.setStyle("-fx-font-size: 18px;" + "-fx-font-weight: bold;" + "-fx-text-fill: red;"
        );
        pausedLabel.setVisible(false);

        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(event -> {
                togglePaused(pauseButton);
            }
        );

        Button quitButton =
                new Button("Quit");
        quitButton.setOnAction(event -> {
            stop();
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });
        Button backButton =
                new Button("Back to Menu");
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
    }

    // Player 1 = WASD, Player 2 = Arrow keys
    private void setupControls(StackPane root) {
        root.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.P) {
                togglePaused(null);
                event.consume();
                return;
            }

            switch (event.getCode()) {
                case A -> gameState.handleCommand(player1LeftCommand);
                case D -> gameState.handleCommand(player1RightCommand);
                case S -> gameState.handleCommand(player1DownCommand);
                case W -> gameState.handleCommand(player1RotateCommand);

                case LEFT -> gameState.handleCommand(player2LeftCommand);
                case RIGHT -> gameState.handleCommand(player2RightCommand);
                case DOWN -> gameState.handleCommand(player2DownCommand);
                case UP -> gameState.handleCommand(player2RotateCommand);
                default -> {
                    return;
                }
            }
            event.consume();
        });
    }

    private void togglePaused(Button pauseButton) {
        // paused = !paused;
        gameState = gameState.toggleState();
        boolean paused = gameState.isPaused();

        pausedLabel.setVisible(paused);
        if (pauseButton != null) {
            pauseButton.setText(paused ? "Resume" : "Pause");
        }
    }

    void updatePlayers(int tickMs) {
        if (!player1.isGameOver()) {
            player1.tick(tickMs);
        }
        if (!player2.isGameOver()) {
            player2.tick(tickMs);
        }
    }

    private void startGameLoop() {

        final int tickMs = 30;
        gameLoop = new Timeline(new KeyFrame(Duration.millis(tickMs), event -> {
            gameState.handleTick(this, tickMs);
            if (player1.isGameOver() && player2.isGameOver()) {
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