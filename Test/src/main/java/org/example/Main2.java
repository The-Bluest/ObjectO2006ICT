package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Optional;

public class Main2 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private StackPane root;

    private final double widthBase = 500;

    private double heightBase;

    private final HighScoreManager highScoreManager = new HighScoreManager();
    private Settings gameSettings;

    private void showMainScreen() {

        VBox mainScreen = new VBox(10);

        mainScreen.setPadding(new Insets(20));

        Label label = new Label("Main Screen");


        Button startButton = new Button("Start Game");

        startButton.setOnAction(e -> beginGame());

        Button splashButton = new Button("Credits");

        splashButton.setOnAction(e -> showSplashScreen());

        Button highScoreButton = new Button("High Scores");

        highScoreButton.setOnAction(
                e -> showHighScoreScreen()
        );


        Button configButton = new Button("Configuration");

        configButton.setOnAction(
                e -> showConfigScreen()
        );


        Button exitButton = new Button("Exit");

        exitButton.setOnAction(e -> {
            System.exit(0);
        });


        mainScreen.getChildren().addAll(
                label,
                startButton,
                highScoreButton,
                configButton,
                splashButton,
                exitButton
        );

        root.getChildren().setAll(mainScreen);
    }
    private void beginGame() {
        Optional<String> playerName = requestPlayerName();
        if (playerName.isEmpty()) {
            return;
        }

        try {

            // Update window size according to settings
            root.getScene().getWindow().setHeight(
                    gameSettings.getGameHeight() * Tetris.size + 40
            );

            root.getScene().getWindow().setWidth(
                    gameSettings.getGameWidth() * Tetris.size + 180
            );

            new Tetris(gameSettings, highScoreManager).start(
                    root,
                    playerName.get(),
                    this::showMainScreen
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Optional<String> requestPlayerName() {
        TextInputDialog dialog = new TextInputDialog("Player");
        dialog.setTitle("Start Game");
        dialog.setHeaderText("Enter your name for the leaderboard");
        dialog.setContentText("Player name:");
        dialog.initOwner(root.getScene().getWindow());

        return dialog.showAndWait()
                .map(String::trim)
                .filter(name -> !name.isBlank());
    }

    private void showSplashScreen(){
        VBox splashScreen = new VBox(10);

        splashScreen.setPadding(new Insets(20));

        Label label = new Label("Group 13 of 2006ICT\n" +
                "Members: \n" +
                "s5260128 Anton Navarro-Carneiro git: The-Bluest\n" +
                "s2990754 Sonny Giosserano git: 1Stunza\n" +
                "s5327758 Yangzhe Lin git: RoyL919\n" +
                "s5496712 Weitao Zhang git: ChiyouItou\n" +
                "s5391319 Dilkash Wadhwani git:Dilkash09\n");
        VBox sScreen =
                new VBox(10);

        sScreen.setPadding(
                new Insets(20)
        );

        sScreen.getChildren().add(label);
        Button back =
                new Button("Return to Menu");

        back.setOnAction(
                e -> showMainScreen()
        );


        sScreen.getChildren().add(back);
        root.getChildren().setAll(sScreen);

    }


    private void showConfigScreen() {

        VBox configScreen = new VBox(10);

        configScreen.setPadding(new Insets(20));

        Label label = new Label("Config");

        // Game Height

        Label heightLabel =
                new Label("Game Height");

        Slider height =
                new Slider(
                        8,
                        24,
                        gameSettings.getGameHeight()
                );

        height.setMajorTickUnit(1);

        height.setMinorTickCount(0);

        height.setSnapToTicks(true);

        height.setShowTickMarks(true);

        height.setShowTickLabels(true);


        height.valueProperty().addListener(
                (observable, oldValue, newValue) -> {

                    gameSettings.setGameHeight(
                            newValue.intValue()
                    );
                }
        );

        // Game Width

        Label widthLabel =
                new Label("Game Width");

        Slider width =
                new Slider(
                        4,
                        12,
                        gameSettings.getGameWidth()
                );

        width.setMajorTickUnit(1);

        width.setMinorTickCount(0);

        width.setSnapToTicks(true);

        width.setShowTickMarks(true);

        width.setShowTickLabels(true);


        width.valueProperty().addListener(
                (observable, oldValue, newValue) -> {

                    gameSettings.setGameWidth(
                            newValue.intValue()
                    );
                }
        );

        // Game Speed

        Label speedLabel =
                new Label("Game Speed");

        Slider speed =
                new Slider(
                        1,
                        10,
                        gameSettings.getGameSpeed()
                );

        speed.setMajorTickUnit(1);

        speed.setMinorTickCount(0);

        speed.setShowTickMarks(true);

        speed.setShowTickLabels(true);


        speed.valueProperty().addListener(
                (observable, oldValue, newValue) -> {

                    gameSettings.setGameSpeed(
                            newValue.doubleValue()
                    );
                }
        );


        //game difficulty
        Label difLabel = new Label("Difficulty");
        Slider difficulty = new Slider(
                Settings.MIN_DIFFICULTY,
                Settings.MAX_DIFFICULTY,
                gameSettings.getDifficulty()
        );
        difficulty.setMajorTickUnit(1);
        difficulty.setMinorTickCount(0);
        difficulty.setSnapToTicks(true);
        difficulty.setShowTickMarks(true);
        difficulty.setShowTickLabels(true);
        difficulty.valueProperty().addListener(
                ((observable, oldValue, newValue) -> {
                    gameSettings.setDifficulty(
                            newValue.intValue()
                    );
                })
        );

        // Music

        CheckBox music =
                new CheckBox("Enable Music?");

        music.setSelected(
                gameSettings.isMusicEnabled()
        );


        music.setOnAction(event -> {

            gameSettings.setMusicEnabled(
                    music.isSelected()
            );
        });

        // SFX

        CheckBox sfx =
                new CheckBox("Enable sfx?");

        sfx.setSelected(
                gameSettings.isSfxEnabled()
        );

        CheckBox AiPlay =
                new CheckBox("Enable AI Play?");

        AiPlay.setSelected(
                settings.isAiEnabled()
        );

        AiPlay.setOnAction(event->
                settings.setAiPlay(AiPlay.isSelected())
        );



        sfx.setOnAction(event -> {

            gameSettings.setSfxEnabled(
                    sfx.isSelected()
            );
        });

        // Back button

        Button back =
                new Button("Save & Return to Menu");

        back.setOnAction(
                e -> {
                    saveSettings();
                    showMainScreen();
                }
        );


        configScreen.getChildren().addAll(
                label,
                heightLabel,
                height,
                widthLabel,
                width,
                speedLabel,
                speed,
                difLabel,
                difficulty,
                music,
                sfx,
                AiPlay,
                back
        );

        root.getChildren().setAll(configScreen);
    }

    private void saveSettings() {
        try {
            gameSettings.save();
        } catch (IllegalStateException exception) {
            exception.printStackTrace();
        }
    }

    private void showHighScoreScreen() {
        List<HighScoreEntry> highScores = highScoreManager.loadScores();

        VBox HScreen =
                new VBox(10);

        HScreen.setPadding(
                new Insets(20)
        );

        Label label =
                new Label("High Score Screen");

        HScreen.getChildren().add(label);


        if (highScores.isEmpty()) {
            HScreen.getChildren().add(new Label("No scores yet."));
        } else {
            for (int index = 0; index < highScores.size(); index++) {
                HighScoreEntry entry = highScores.get(index);
                HScreen.getChildren().add(
                        new Label(
                                (index + 1) + ". "
                                        + entry.getPlayerName()
                                        + " - "
                                        + entry.getScore()
                        )
                );
            }
        }


        Button back =
                new Button("Return to Menu");

        back.setOnAction(
                e -> showMainScreen()
        );


        HScreen.getChildren().add(back);

        root.getChildren().setAll(HScreen);
    }


    @Override
    public void start(Stage primaryStage) {

        // Create Settings once
        gameSettings = Settings.load();

        // Calculate initial window height
        heightBase =
                gameSettings.getGameHeight() * Tetris.size + 20;


        root = new StackPane();

        // Splash screen

        Stage splashStage =
                new Stage(
                        StageStyle.UNDECORATED
                );


        ImageView splashImage =
                new ImageView(
                        new Image(
                                getClass()
                                        .getResource(
                                                "/Wish.png" //cute, please make sure what ever you link actually exists
                                        )
                                        .toExternalForm()
                        )
                );


        splashImage.setFitHeight(300);

        splashImage.setFitWidth(300);

        splashImage.setPreserveRatio(true);

        splashImage.setSmooth(true);


        Label loadingLabel =
                new Label("Group 13 of 2006ICT_3265");

        loadingLabel.setTextFill(Color.BLACK);



        StackPane splashLayout =
                new StackPane(
                        splashImage,
                        loadingLabel
                );


        Scene splashScene =
                new Scene(
                        splashLayout,
                        300,
                        300
                );


        splashStage.setScene(
                splashScene
        );

        splashStage.show();

        // Loading

        Task<Void> loadTask =
                new Task<>() {

                    @Override
                    protected Void call()
                            throws Exception {

                        Thread.sleep(3000);

                        return null;
                    }


                    @Override
                    protected void succeeded() {

                        Platform.runLater(() -> {

                            splashStage.close();

                            primaryStage.show();

                            showMainScreen();
                        });
                    }
                };


        new Thread(loadTask).start();

        // Main Scene

        Scene scene =
                new Scene(
                        root,
                        widthBase,
                        heightBase
                );


        primaryStage.setTitle(
                "Tetris OOSD!"
        );

        primaryStage.setScene(scene);
    }
}
