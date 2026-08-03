package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        Scene scene = new Scene(root, widthBase, heightBase);
        showMainScreen();
        primaryStage.setTitle("Tetris OOSD!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
