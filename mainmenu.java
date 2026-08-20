package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class mainmenu {

    public void show(Stage stage) {

        Text title = new Text("Main Menu");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button playButton = new Button("Play");
        Button configButton = new Button("Configuration");
        Button highScoreButton = new Button("High Scores");
        Button exitButton = new Button("Exit");

        playButton.setPrefWidth(160);
        configButton.setPrefWidth(160);
        highScoreButton.setPrefWidth(160);
        exitButton.setPrefWidth(160);

        VBox menu = new VBox(
                25,
                title,
                playButton,
                configButton,
                highScoreButton,
                exitButton
        );

        menu.setAlignment(Pos.CENTER);

        Scene scene = new Scene(menu, 400, 500);

        // Play
        playButton.setOnAction(event -> {
            tetris game = new tetris();
            game.start(stage);
        });

        // Configuration
        configButton.setOnAction(event -> {
            configurationScreen configurationScreen = new configurationScreen();
            configurationScreen.show(stage);
        });

        // High Scores
        highScoreButton.setOnAction(event -> {
            highScoreScreen highScoreScreen = new highScoreScreen();
            highScoreScreen.show(stage);
        });

        // Exit confirmation
        exitButton.setOnAction(event -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText("Exit Tetris?");
            alert.setContentText("Are you sure you want to exit?");

            alert.showAndWait().ifPresent(response -> {
                if (response.getText().equals("OK")) {
                    stage.close();
                }
            });
        });

        stage.setTitle("Tetris");
        stage.setScene(scene);
        stage.show();
    }
}