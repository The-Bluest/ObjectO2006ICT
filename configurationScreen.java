package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class configurationScreen {

    public void show(Stage stage) {

        Label title = new Label("Configuration");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");


        Label widthLabel = new Label("Field Width (No of cells):");

        Slider widthSlider = new Slider(5, 15, 10);
        widthSlider.setMajorTickUnit(1);
        widthSlider.setMinorTickCount(0);
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setSnapToTicks(true);

        Label widthValue = new Label("10");

        widthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            widthValue.setText(String.valueOf(newValue.intValue()));
        });


        Label heightLabel = new Label("Field Height (No of cells):");

        Slider heightSlider = new Slider(15, 30, 20);
        heightSlider.setMajorTickUnit(1);
        heightSlider.setMinorTickCount(0);
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setSnapToTicks(true);

        Label heightValue = new Label("20");

        heightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            heightValue.setText(String.valueOf(newValue.intValue()));
        });


        Label levelLabel = new Label("Game Level:");

        Slider levelSlider = new Slider(1, 10, 1);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setMinorTickCount(0);
        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setSnapToTicks(true);

        Label levelValue = new Label("1");

        levelSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            levelValue.setText(String.valueOf(newValue.intValue()));
        });


        Label musicLabel = new Label("Music (On/Off):");
        CheckBox musicCheckBox = new CheckBox();
        musicCheckBox.setSelected(true);

        Label musicStatus = new Label("On");

        musicCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            musicStatus.setText(newValue ? "On" : "Off");
        });


        Label soundLabel = new Label("Sound Effect (On/Off):");
        CheckBox soundCheckBox = new CheckBox();
        soundCheckBox.setSelected(true);

        Label soundStatus = new Label("On");

        soundCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            soundStatus.setText(newValue ? "On" : "Off");
        });


        Label aiLabel = new Label("AI Play (On/Off):");
        CheckBox aiCheckBox = new CheckBox();

        Label aiStatus = new Label("Off");

        aiCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            aiStatus.setText(newValue ? "On" : "Off");
        });


        Label extendLabel = new Label("Extend Mode (On/Off):");
        CheckBox extendCheckBox = new CheckBox();

        Label extendStatus = new Label("Off");

        extendCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            extendStatus.setText(newValue ? "On" : "Off");
        });


        GridPane grid = new GridPane();

        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        grid.add(widthLabel, 0, 0);
        grid.add(widthSlider, 1, 0);
        grid.add(widthValue, 2, 0);

        grid.add(heightLabel, 0, 1);
        grid.add(heightSlider, 1, 1);
        grid.add(heightValue, 2, 1);

        grid.add(levelLabel, 0, 2);
        grid.add(levelSlider, 1, 2);
        grid.add(levelValue, 2, 2);

        grid.add(musicLabel, 0, 3);
        grid.add(musicCheckBox, 1, 3);
        grid.add(musicStatus, 2, 3);

        grid.add(soundLabel, 0, 4);
        grid.add(soundCheckBox, 1, 4);
        grid.add(soundStatus, 2, 4);

        grid.add(aiLabel, 0, 5);
        grid.add(aiCheckBox, 1, 5);
        grid.add(aiStatus, 2, 5);

        grid.add(extendLabel, 0, 6);
        grid.add(extendCheckBox, 1, 6);
        grid.add(extendStatus, 2, 6);


        Button backButton = new Button("Back");
        backButton.setPrefWidth(120);

        backButton.setOnAction(event -> {

            mainmenu mainMenu = new mainmenu();
            mainMenu.show(stage);

        });


        VBox layout = new VBox(
            30,
            title,
            grid,
            backButton
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));


        Scene scene = new Scene(layout, 600, 550);

        stage.setTitle("Configuration");
        stage.setScene(scene);
        stage.show();
    }
}