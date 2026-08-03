package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main2 extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private StackPane root;
    private final double widthBase = 400;
    private final double heightBase = 300;

    private void showMainScreen() {
        VBox mainScreen = new VBox(10);
        mainScreen.setPadding(new Insets(20));
        Label label = new Label("Main Screen");


        Button startButton = new Button("Start Game");
        //this button doesn't link anywhere
        //startButton.setOnAction(e->GameScreenMethodHere());

        Button configButton = new Button("Configuration");
        //configButton.setOnAction(e->ConfigScreenMethodHere());

        Button exitButton = new Button("Exit");


        mainScreen.getChildren().addAll(label, startButton, configButton, exitButton);
        root.getChildren().addAll(mainScreen);
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
