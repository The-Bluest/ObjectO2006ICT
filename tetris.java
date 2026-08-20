package org.example;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class tetris{
    //variables
    public static final int move = 25; //Settings dependent
    public static final int size = 25;// settings dependent if movable by 1 value
    public static int xMax = 250; //settings dependent if deliberately altered
    public static int yMax = 500;// settings dependent if deliberately altered
    public static int [][] mesh = new int [xMax/size][yMax/size];
	public void start(Stage stage) {

		resetGame();

		scene = new Scene(group, xMax + 150, yMax);

		scoreText.setText("Score: 0");
		scoreText.setX(xMax + 25);
		scoreText.setY(50);
		group.getChildren().add(scoreText);

		pauseText.setText("PAUSED");
		pauseText.setX(xMax / 2 - 35);
		pauseText.setY(yMax / 2);
		pauseText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		pauseText.setVisible(false);

		gameOverText.setText("GAME OVER");
		gameOverText.setX(xMax / 2 - 55);
		gameOverText.setY(yMax / 2);
		gameOverText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		gameOverText.setVisible(false);

		group.getChildren().addAll(pauseText, gameOverText);

		// Create a tetromino
		object = controller.makeShape();
		System.out.println("Spawned shape: " + object.getName());

		// Add the tetromino to the screen
		group.getChildren().addAll(
			object.a,
			object.b,
			object.c,
			object.d
		);

		// Enable keyboard controls
		moveOnKeyPress(object);

		startGameTimer();

		// Show window
		stage.setScene(scene);
		stage.setTitle("Tetris Game Play");
		stage.show();

		Button backButton = new Button("Back");

		backButton.setLayoutX(xMax + 25);
		backButton.setLayoutY(450);

		backButton.setOnAction(event -> {

			// Pause while asking
			gameTimer.pause();

			if (currentFallAnimation != null) {
				currentFallAnimation.pause();
			}

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

			alert.setTitle("Return to Main Menu");
			alert.setHeaderText("Leave current game?");
			alert.setContentText("Your current game will be lost.");

			alert.showAndWait().ifPresent(response -> {

				if (response == ButtonType.OK) {

					gameTimer.stop();

					if (currentFallAnimation != null) {
						currentFallAnimation.stop();
					}

					mainmenu mainMenu = new mainmenu();
					mainMenu.show(stage);

				} else {

					// Continue playing
					gameTimer.play();

					if (currentFallAnimation != null) {
						currentFallAnimation.play();
					}
				}
			});
		});

		group.getChildren().add(backButton);
	}
    private static Pane group = new Pane();
    private static form object;
    //private static Scene scene = new Scene(group, xMax + 150, yMax);
	private static Scene scene;
    public static int score = 0;
	private static Text scoreText = new Text();
    private static int top = 0;
    private static boolean game = true;
    private static form nextObj = controller.makeShape();
    private static int linesNo = 0;
	private Timeline gameTimer;
	private boolean isAnimating = false;
	private int pendingDownMoves = 0;
	private ParallelTransition currentFallAnimation;
	private boolean paused = false;
	private static Text pauseText = new Text();
	private static Text gameOverText = new Text();


    //make scene start game maybe trigger from screen
    //public void main (String[] args) {launch(args);} 
    //left 18min score and text left. 

    
	private void moveOnKeyPress(form form) { //org.example.controller heavy movement of piece.
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {

				if (!game) {
					return;
				}

				if (paused && event.getCode() != KeyCode.P) {
					return;
				}

				switch (event.getCode()) {
				case RIGHT:
					controller.moveRight(form);
					break;
				case DOWN:

					if (isAnimating && currentFallAnimation != null) {

						// Finish the current visual movement immediately
						currentFallAnimation.jumpTo(currentFallAnimation.getTotalDuration());
						currentFallAnimation.stop();

						object.a.setY(object.a.getY() + size);
						object.b.setY(object.b.getY() + size);
						object.c.setY(object.c.getY() + size);
						object.d.setY(object.d.getY() + size);

						object.a.setTranslateY(0);
						object.b.setTranslateY(0);
						object.c.setTranslateY(0);
						object.d.setTranslateY(0);

						isAnimating = false;
						currentFallAnimation = null;
					}

					moveOneRow(60);

					break;
				case LEFT:
					controller.moveLeft(form);
					break;
				case UP:
					moveTurn(form); //divine comdey
					break;

				case P:
					paused = !paused;

					if (paused) {
						gameTimer.pause();

						if (currentFallAnimation != null) {
							currentFallAnimation.pause();
						}
						pauseText.setVisible(true);
						System.out.println("PAUSED");

					} else {
						gameTimer.play();

						if (currentFallAnimation != null) {
							currentFallAnimation.play();
						}
						pauseText.setVisible(false);
						System.out.println("RESUMED");
					}

					break;
				}
			}
		});
	}
    private void moveTurn(form form) { //so the jist of this nightmare of comphrension is that each piece moves depending on it's placement to different area according to the sages this is what gives org.example.tetris it's neat juggling abilitiy.
		int f = form.form;
		Rectangle a = form.a;
		Rectangle b = form.b;
		Rectangle c = form.c;
		Rectangle d = form.d;
		switch (form.getName()) {
		case "l":
			if (f == 1 && cB(a, 1, -1) && cB(c, -1, -1) && cB(d, -2, -2)) {
				moveRight(form.a);
				moveDown(form.a);
				moveDown(form.c);
				moveLeft(form.c);
				moveDown(form.d);
				moveDown(form.d);
				moveLeft(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, -2, 2)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.c);
				moveUp(form.c);
				moveLeft(form.d);
				moveLeft(form.d);
				moveUp(form.d);
				moveUp(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, 1) && cB(c, 1, 1) && cB(d, 2, 2)) {
				moveLeft(form.a);
				moveUp(form.a);
				moveUp(form.c);
				moveRight(form.c);
				moveUp(form.d);
				moveUp(form.d);
				moveRight(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 2, -2)) {
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.c);
				moveDown(form.c);
				moveRight(form.d);
				moveRight(form.d);
				moveDown(form.d);
				moveDown(form.d);
				form.changeForm();
				break;
			}
			break;
		case "ll":
			if (f == 1 && cB(a, 1, -1) && cB(c, 1, 1) && cB(b, 2, 2)) {
				moveRight(form.a);
				moveDown(form.a);
				moveUp(form.c);
				moveRight(form.c);
				moveUp(form.b);
				moveUp(form.b);
				moveRight(form.b);
				moveRight(form.b);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, -1, -1) && cB(b, 2, -2) && cB(c, 1, -1)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveRight(form.b);
				moveRight(form.b);
				moveDown(form.b);
				moveDown(form.b);
				moveRight(form.c);
				moveDown(form.c);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, 1) && cB(c, -1, -1) && cB(b, -2, -2)) {
				moveLeft(form.a);
				moveUp(form.a);
				moveDown(form.c);
				moveLeft(form.c);
				moveDown(form.b);
				moveDown(form.b);
				moveLeft(form.b);
				moveLeft(form.b);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(b, -2, 2) && cB(c, -1, 1)) {
				moveUp(form.a);
				moveRight(form.a);
				moveLeft(form.b);
				moveLeft(form.b);
				moveUp(form.b);
				moveUp(form.b);
				moveLeft(form.c);
				moveUp(form.c);
				form.changeForm();
				break;
			}
			break;
		case "square":
			break;
		case "s":
			if (f == 1 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.c);
				moveUp(form.c);
				moveUp(form.d);
				moveUp(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.c);
				moveDown(form.c);
				moveDown(form.d);
				moveDown(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.c);
				moveUp(form.c);
				moveUp(form.d);
				moveUp(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.c);
				moveDown(form.c);
				moveDown(form.d);
				moveDown(form.d);
				form.changeForm();
				break;
			}
			break;
		case "t":
			if (f == 1 && cB(a, 1, 1) && cB(d, -1, -1) && cB(c, -1, 1)) {
				moveUp(form.a);
				moveRight(form.a);
				moveDown(form.d);
				moveLeft(form.d);
				moveLeft(form.c);
				moveUp(form.c);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, 1, -1) && cB(d, -1, 1) && cB(c, 1, 1)) {
				moveRight(form.a);
				moveDown(form.a);
				moveLeft(form.d);
				moveUp(form.d);
				moveUp(form.c);
				moveRight(form.c);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, -1, -1) && cB(d, 1, 1) && cB(c, 1, -1)) {
				moveDown(form.a);
				moveLeft(form.a);
				moveUp(form.d);
				moveRight(form.d);
				moveRight(form.c);
				moveDown(form.c);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, -1, 1) && cB(d, 1, -1) && cB(c, -1, -1)) {
				moveLeft(form.a);
				moveUp(form.a);
				moveRight(form.d);
				moveDown(form.d);
				moveDown(form.c);
				moveLeft(form.c);
				form.changeForm();
				break;
			}
			break;
		case "zig":
			if (f == 1 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
				moveUp(form.b);
				moveRight(form.b);
				moveLeft(form.c);
				moveUp(form.c);
				moveLeft(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
				moveDown(form.b);
				moveLeft(form.b);
				moveRight(form.c);
				moveDown(form.c);
				moveRight(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
				moveUp(form.b);
				moveRight(form.b);
				moveLeft(form.c);
				moveUp(form.c);
				moveLeft(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
				moveDown(form.b);
				moveLeft(form.b);
				moveRight(form.c);
				moveDown(form.c);
				moveRight(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			break;
		case "line":
			if (f == 1 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
				moveUp(form.a);
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.a);
				moveUp(form.b);
				moveRight(form.b);
				moveDown(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 2 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
				moveDown(form.a);
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.a);
				moveDown(form.b);
				moveLeft(form.b);
				moveUp(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			if (f == 3 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
				moveUp(form.a);
				moveUp(form.a);
				moveRight(form.a);
				moveRight(form.a);
				moveUp(form.b);
				moveRight(form.b);
				moveDown(form.d);
				moveLeft(form.d);
				form.changeForm();
				break;
			}
			if (f == 4 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
				moveDown(form.a);
				moveDown(form.a);
				moveLeft(form.a);
				moveLeft(form.a);
				moveDown(form.b);
				moveLeft(form.b);
				moveUp(form.d);
				moveRight(form.d);
				form.changeForm();
				break;
			}
			break;
		}
	}
    
    	private void moveDown(Rectangle rect) {
		if (rect.getY() + move < yMax)
			rect.setY(rect.getY() + move);

	}

	private void moveRight(Rectangle rect) {
		if (rect.getX() + move <= xMax - size)
			rect.setX(rect.getX() + move);
	}

	private void moveLeft(Rectangle rect) {
		if (rect.getX() - move >= 0)
			rect.setX(rect.getX() - move);
	}

    	private void moveUp(Rectangle rect) {
		if (rect.getY() - move > 0)
			rect.setY(rect.getY() - move);
	}

    private boolean cB(Rectangle rect, int x, int y) {
		boolean xb = false;
		boolean yb = false;
		if (x >= 0)
			xb = rect.getX() + x * move <= xMax - size;
		if (x < 0)
			xb = rect.getX() + x * move >= 0;
		if (y >= 0)
			yb = rect.getY() - y * move > 0;
		if (y < 0)
			yb = rect.getY() + y * move < yMax;
		return xb && yb && mesh[((int) rect.getX() / size) + x][((int) rect.getY() / size) - y] == 0;
	}

	private boolean canMoveDown(form piece) {

		Rectangle[] blocks = {
			piece.a,
			piece.b,
			piece.c,
			piece.d
		};

		for (Rectangle block : blocks) {

			int column = (int) block.getX() / size;
			int row = (int) block.getY() / size;

			// Bottom of board
			if (row + 1 >= yMax / size) {
				return false;
			}

			// Another landed block directly underneath
			if (mesh[column][row + 1] != 0) {
				return false;
			}
		}

		return true;
	}

	private void lockPiece(form piece) {

		Rectangle[] blocks = {
			piece.a,
			piece.b,
			piece.c,
			piece.d
		};

		for (Rectangle block : blocks) {

			int column = (int) block.getX() / size;
			int row = (int) block.getY() / size;

			mesh[column][row] = 1;
		}
	}

	private void startGameTimer() {

		gameTimer = new Timeline(
				new KeyFrame(Duration.millis(500), event -> {

					if (!isAnimating) {
						moveOneRow(450);
					}

				})
		);

		gameTimer.setCycleCount(Timeline.INDEFINITE);
		gameTimer.play();
	}

	private void spawnNewPiece() {

		object = controller.makeShape();

		if (!canSpawn(object)) {
			gameOver();
			return;
		}

		System.out.println("Spawned shape: " + object.getName());

		group.getChildren().addAll(
			object.a,
			object.b,
			object.c,
			object.d
		);

		moveOnKeyPress(object);
	}

	private boolean canSpawn(form piece) {

		Rectangle[] blocks = {
			piece.a,
			piece.b,
			piece.c,
			piece.d
		};

		for (Rectangle block : blocks) {

			int column = (int) block.getX() / size;
			int row = (int) block.getY() / size;

			if (mesh[column][row] != 0) {
				return false;
			}
		}

		return true;
	}

	private void moveOneRow(int duration) {

		if (isAnimating) {
			return;
		}

		if (!canMoveDown(object)) {
			lockPiece(object);
			System.out.println("Piece locked");
			int clearedRows = clearFullRows();
			updateScore(clearedRows);
			spawnNewPiece();
			return;
		}

		isAnimating = true;

		TranslateTransition moveA =
			new TranslateTransition(Duration.millis(duration), object.a);

		TranslateTransition moveB =
			new TranslateTransition(Duration.millis(duration), object.b);

		TranslateTransition moveC =
			new TranslateTransition(Duration.millis(duration), object.c);

		TranslateTransition moveD =
			new TranslateTransition(Duration.millis(duration), object.d);

		moveA.setByY(size);
		moveB.setByY(size);
		moveC.setByY(size);
		moveD.setByY(size);

		// Constant falling speed
		moveA.setInterpolator(Interpolator.LINEAR);
		moveB.setInterpolator(Interpolator.LINEAR);
		moveC.setInterpolator(Interpolator.LINEAR);
		moveD.setInterpolator(Interpolator.LINEAR);

		currentFallAnimation =
			new ParallelTransition(
				moveA,
				moveB,
				moveC,
				moveD
			);

		currentFallAnimation.setOnFinished(e -> {

			object.a.setY(object.a.getY() + size);
			object.b.setY(object.b.getY() + size);
			object.c.setY(object.c.getY() + size);
			object.d.setY(object.d.getY() + size);

			object.a.setTranslateY(0);
			object.b.setTranslateY(0);
			object.c.setTranslateY(0);
			object.d.setTranslateY(0);

			isAnimating = false;
			currentFallAnimation = null;
		});

		currentFallAnimation.play();
	}

	private void gameOver() {

		game = false;
		paused = false;
		pendingDownMoves = 0;
		isAnimating = false;

		if (gameTimer != null) {
			gameTimer.stop();
		}

		if (currentFallAnimation != null) {
			currentFallAnimation.stop();
		}

		pauseText.setVisible(false);

		gameOverText.setText(
			"GAME OVER\nScore: " + score
		);

		gameOverText.setVisible(true);

		System.out.println("GAME OVER");
	}

	private int clearFullRows() {

		int rows = yMax / size;
		int columns = xMax / size;
		int clearedRows = 0;

		for (int row = rows - 1; row >= 0; row--) {

			boolean full = true;

			for (int column = 0; column < columns; column++) {
				if (mesh[column][row] == 0) {
					full = false;
					break;
				}
			}

			if (full) {
				clearRow(row);
				clearedRows++;

				// Check the same row again after rows move down
				row++;
			}
		}

		return clearedRows;
	}

	private void clearRow(int row) {

		// Remove blocks visually from this row
		group.getChildren().removeIf(node -> {

			if (node instanceof Rectangle rect) {
				int rectRow = (int) Math.round(rect.getY() / size);

				return rectRow == row;
			}

			return false;
		});

		// Move every block above the cleared row down
		for (javafx.scene.Node node : group.getChildren()) {

			if (node instanceof Rectangle rect) {

				int rectRow = (int) Math.round(rect.getY() / size);

				if (rectRow < row) {
					rect.setY(rect.getY() + size);
				}
			}
		}

		// Move mesh rows down
		for (int r = row; r > 0; r--) {

			for (int column = 0; column < xMax / size; column++) {
				mesh[column][r] = mesh[column][r - 1];
			}
		}

		// Empty the top row
		for (int column = 0; column < xMax / size; column++) {
			mesh[column][0] = 0;
		}

		System.out.println("Row cleared");
	}

	private void updateScore(int clearedRows) {

		switch (clearedRows) {

			case 1:
				score += 100;
				break;

			case 2:
				score += 300;
				break;

			case 3:
				score += 500;
				break;

			case 4:
				score += 800;
				break;
		}

		if (clearedRows > 0) {
			linesNo += clearedRows;

			scoreText.setText("Score: " + score);

			System.out.println(
					"Rows cleared: " + clearedRows
							+ " | Score: " + score
							+ " | Lines: " + linesNo
			);
		}
	}

	private void resetGame() {

		// New empty board
		mesh = new int[xMax / size][yMax / size];

		// Reset game values
		score = 0;
		linesNo = 0;
		game = true;
		paused = false;
		isAnimating = false;
		pendingDownMoves = 0;


		if (gameTimer != null) {
			gameTimer.stop();
		}

		if (currentFallAnimation != null) {
			currentFallAnimation.stop();
			currentFallAnimation = null;
		}

		group = new Pane();
	}
}