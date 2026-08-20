package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class highScoreScreen {

    public void show(Stage stage) {

        Text title = new Text("Top 10 High Scores");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Score table
        GridPane scoreGrid = new GridPane();

        scoreGrid.setHgap(30);
        scoreGrid.setVgap(8);
        scoreGrid.setAlignment(Pos.CENTER);

        Text rankHeading = new Text("Rank");
        Text nameHeading = new Text("Name");
        Text scoreHeading = new Text("Score");

        rankHeading.setStyle("-fx-font-weight: bold;");
        nameHeading.setStyle("-fx-font-weight: bold;");
        scoreHeading.setStyle("-fx-font-weight: bold;");

        scoreGrid.add(rankHeading, 0, 0);
        scoreGrid.add(nameHeading, 1, 0);
        scoreGrid.add(scoreHeading, 2, 0);

        //  high score data
        String[] names = {
                "Player 1254",
                "Player 1555",
                "Player 15494",
                "Player 2544",
                "Player 951",
                "Player 4894",
                "Player 848",
                "Player 461",
                "Player 354",
                "Player 1494"
        };

        int[] scores = {
                10000,
                9000,
                8000,
                7000,
                6000,
                5000,
                4000,
                3000,
                2000,
                1000
        };

        for (int i = 0; i < 10; i++) {

            Text rank = new Text((i + 1) + ".");
            Text name = new Text(names[i]);
            Text score = new Text(String.valueOf(scores[i]));

            scoreGrid.add(rank, 0, i + 1);
            scoreGrid.add(name, 1, i + 1);
            scoreGrid.add(score, 2, i + 1);
        }

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefWidth(120);

        backButton.setOnAction(event -> {
            mainmenu mainMenu = new mainmenu();
            mainMenu.show(stage);
        });

        VBox layout = new VBox(
                25,
                title,
                scoreGrid,
                backButton
        );

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 500);

        stage.setTitle("Top 10 High Scores");
        stage.setScene(scene);
        stage.show();
    }
}