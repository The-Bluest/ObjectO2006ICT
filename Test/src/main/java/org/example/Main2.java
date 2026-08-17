package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;


//import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main2 extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private ArrayList<Integer> highscores;
    private ObjectMapper mapper = new ObjectMapper();
    private File highFile = new File("./thing.txt");
    private StackPane root;
    private final double widthBase = 500;
    private final double heightBase = 400;

    private int gameHeight = 16;
    private int gameWidth = 8;
    private double gameSpeed = 5;
    private boolean musicBool = true;
    private boolean sfxBool = true;

    private void showMainScreen() {
        boolean err = false;
        highscores = new ArrayList<Integer>();
        try {

            BufferedReader bf = new BufferedReader(new FileReader(highFile));
            String line = bf.readLine();
            while (line != null) {
                highscores.add(Integer.parseInt(line));
                line = bf.readLine();
            }


        } catch (java.io.FileNotFoundException e) {
            System.out.println("File Not Found");
            err = true;
        } catch (java.io.IOException e) {
            System.out.print("IOException");
            err = true;
        }

        if (err) {
            ArrayList<Integer> output = new ArrayList<Integer>();
            output.add(0);
            output.add(0);
            output.add(0);
            output.add(0);
            output.add(0);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./thing.txt"))) {
                for (Integer out : output) {
                    writer.write(out.toString());
                    writer.newLine();
                }
            } catch (java.io.IOException e) {
                System.out.println("Error in print???");
            }
        }


        VBox mainScreen = new VBox(10);
        mainScreen.setPadding(new Insets(20));
        Label label = new Label("Main Screen");

        Button startButton = new Button("Start Game");
        //this button doesn't link anywhere
        //startButton.setOnAction(e->GameScreenMethodHere());

        Button highScoreButton = new Button("High Scores");
        highScoreButton.setOnAction(e -> showHighScoreScreen());

        Button configButton = new Button("Configuration");
        configButton.setOnAction(e -> showConfigScreen());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            System.exit(0);
        });

        mainScreen.getChildren().addAll(label, startButton, highScoreButton, configButton, exitButton);
        root.getChildren().setAll(mainScreen);
    }


    private void showConfigScreen() {
        VBox configScreen = new VBox(10);
        configScreen.setPadding(new Insets(20));
        Label label = new Label("Config");
        Label heightLabel = new Label("Game Height");
        Slider height = new Slider(8, 24, gameHeight);
        height.setMajorTickUnit(1);
        height.setMinorTickCount(0);
        height.setSnapToTicks(true);
        height.setShowTickMarks(true);
        height.setShowTickLabels(true);
        height.valueProperty().addListener((observable, oldValue, newValue) -> {
            gameHeight = newValue.intValue();
        });
        Label widthLabel = new Label("Game Width");
        Slider width = new Slider(4, 12, gameWidth);
        width.setMajorTickUnit(1);
        width.setMinorTickCount(0);
        width.setSnapToTicks(true);
        width.setShowTickMarks(true);
        width.setShowTickLabels(true);
        width.valueProperty().addListener((observable, oldValue, newValue) -> {
            gameWidth = newValue.intValue();
        });
        Label speedLabel = new Label("Game Speed");
        Slider speed = new Slider(1, 10, gameSpeed);
        speed.setMajorTickUnit(1);
        speed.setMinorTickCount(0);
        speed.setShowTickMarks(true);
        speed.setShowTickLabels(true);
        speed.valueProperty().addListener((observable, oldValue, newValue) -> {
            gameSpeed = newValue.doubleValue();
        });
        CheckBox music = new CheckBox("Enable Music?");
        music.setSelected(musicBool);
        music.setOnAction(event -> {
            if (music.isSelected()) {
                musicBool = true;
            } else {
                musicBool = false;
            }
        });
        CheckBox sfx = new CheckBox("Enable sfx?");
        sfx.setSelected(sfxBool);
        sfx.setOnAction(event -> {
            if (sfx.isSelected()) {
                sfxBool = true;
            } else {
                sfxBool = false;
            }
        });
        Button back = new Button("Return to Menu");
        back.setOnAction(e -> showMainScreen());

        configScreen.getChildren().addAll(label, heightLabel, height, widthLabel, width, speedLabel, speed, music, sfx, back);
        root.getChildren().setAll(configScreen);
    }

    private void showHighScoreScreen() {
        VBox HScreen = new VBox(10);
        HScreen.setPadding(new Insets(20));
        Label label = new Label("High Score Screen");
        HScreen.getChildren().add(label);
        int scoreTal = 1;
        for (Integer score : highscores) {
            HScreen.getChildren().add(new Label(scoreTal + ":   " + score));
            scoreTal += 1;
        }
        Button back = new Button("Return to Menu");
        back.setOnAction(e -> showMainScreen());
        HScreen.getChildren().add(back);
        root.getChildren().setAll(HScreen);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();
        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        ImageView splashImage = new ImageView(new Image(getClass().getResource("/Wish.png").toExternalForm()));
        splashImage.setFitHeight(300);
        splashImage.setFitWidth(300);
        splashImage.setPreserveRatio(true);
        splashImage.setSmooth(true);
        Label loadingLabel = new Label("Take your Time");
        loadingLabel.setTextFill(Color.WHITE);

        StackPane splashLayout = new StackPane(splashImage, loadingLabel);
        Scene splashScene = new Scene(splashLayout, 300, 300);
        splashStage.setScene(splashScene);
        splashStage.show();

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
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
        Scene scene = new Scene(root, widthBase, heightBase);
        primaryStage.setTitle("Tetris OOSD!");
        primaryStage.setScene(scene);
    }
}