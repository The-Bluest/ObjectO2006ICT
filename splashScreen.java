package org.example;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class splashScreen {

    public void show(Stage stage) {

        Text title = new Text("TETRIS");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");



        VBox layout = new VBox(
                20,
                title

        );

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 500);

        stage.setTitle("Tetris");
        stage.setScene(scene);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));

        delay.setOnFinished(event -> {
            mainmenu mainMenu = new mainmenu();
            mainMenu.show(stage);
        });

        delay.play();
    }
}