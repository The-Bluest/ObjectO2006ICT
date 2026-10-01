package org.example;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Tetris {
    //variables
    public static final int move = 25;
    public static final int size = 25;
    public static int xMax;
    public static int yMax;
    public static int[][] mesh;
    private static Pane groupe = new Pane();
    private static form object;
    private static Scene scene;
    public static int score = 0;
    private static int top = 0;
    private static boolean game = true;
    private static boolean paused = false;
    private static form nextObj;
    private static int linesNo = 0;
    private static Text pausedText;
    private static Text externalWarningText;
    private static Text musicStatusText;
    private static Text sfxStatusText;
    private static Text difficultyText; // add
    private Runnable onGameOver;
    private int fallInterval;
    private static final int fastFallInterval = 60;
    private static boolean fastFall = false;
    private final Settings settings;
    private final AudioManager audioManager;
    private final AI ai;
    private final ExternalPlayer externalPlayer;
    private final HighScoreManager highScoreManager;

    public Tetris(Settings settings) {
        this(settings, new HighScoreManager());
    }

    public Tetris(Settings settings, HighScoreManager highScoreManager) {
        if (settings == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }
        if (highScoreManager == null) {
            throw new IllegalArgumentException(
                    "High score manager cannot be null");
        }
        this.settings = settings;
        this.audioManager = new AudioManager();
        this.ai = new AI(boardeval.getInstance());
        this.externalPlayer = new ExternalPlayer();
        this.highScoreManager = highScoreManager;
    }


    public void start(StackPane root, Runnable onGameOver) throws Exception {
        start(root, "Anonymous", onGameOver);
    }

    public void start(
            StackPane root,
            String playerName,
            Runnable onGameOver
    ) throws Exception {
        this.onGameOver = onGameOver;
        xMax = settings.getGameWidth() * size;
        yMax = settings.getGameHeight() * size;
        fallInterval = 600 - (int)(settings.getGameSpeed() * 50);
        mesh = new int[xMax / size][yMax / size];

        audioManager.setMusicEnabled(settings.isMusicEnabled());
        audioManager.setSfxEnabled(settings.isSfxEnabled());

        groupe.getChildren().clear();
        score = 0;
        top = 0;
        linesNo = 0;
        game = true;
        paused = false;
        for (int[] a : mesh) {
            Arrays.fill(a, 0);
        }

        root.getChildren().setAll(groupe);
        scene = root.getScene();

        Platform.runLater(() -> {
                    groupe.setFocusTraversable(true);
                    groupe.requestFocus();
                });

        System.out.println("BEGINNING THING");
        nextObj = controller.makeShape();
        System.out.println("THING COMPLETED");

        Line line = new Line(xMax, 0, xMax, yMax);
        Text scoretext = new Text("Score: ");
        scoretext.setStyle("-fx-font: 20 arial;");
        scoretext.setY(50);
        scoretext.setX(xMax + 5);
        Text level = new Text("Lines: ");
        level.setStyle("-fx-font: 20 arial;");
        level.setY(100);
        level.setX(xMax + 5);
        level.setFill(Color.GREEN);
        musicStatusText = new Text(
                "Music: " + (settings.isMusicEnabled() ? "ON" : "OFF")
        );
        musicStatusText.setStyle("-fx-font: 16 arial;");
        musicStatusText.setX(xMax + 5);
        musicStatusText.setY(125);

        sfxStatusText = new Text(
                "Sound: " + (settings.isSfxEnabled() ? "ON" : "OFF")
        );
        sfxStatusText.setStyle("-fx-font: 16 arial;");
        sfxStatusText.setX(xMax + 5);
        sfxStatusText.setY(150);

        // add: difficulty label on the board
        difficultyText = new Text(
                "Difficulty: " + settings.getDifficulty()
        );
        difficultyText.setStyle("-fx-font: 16 arial; -fx-font-weight: bold;");
        difficultyText.setX(xMax + 5);
        difficultyText.setY(175);
        difficultyText.setFill(Color.DARKBLUE);

        pausedText = new Text("PAUSED");
        pausedText.setFill(Color.RED);
        pausedText.setStyle("-fx-font: 40 arial;");
        pausedText.setY(250);
        pausedText.setX(10);
        pausedText.setVisible(false);
        externalWarningText = new Text("External Player server unavailable");
        externalWarningText.setFill(Color.RED);
        externalWarningText.setStyle("-fx-font: 18 arial; -fx-font-weight: bold;");
        externalWarningText.setX((xMax - externalWarningText.getLayoutBounds().getWidth()) / 2);
        externalWarningText.setY(yMax / 2.0);
        externalWarningText.setVisible(false);
        groupe.getChildren().addAll(scoretext, line, level, musicStatusText,
                sfxStatusText, difficultyText, pausedText, externalWarningText);

        form a = nextObj;
        groupe.getChildren().addAll(a.a, a.b, a.c, a.d);
        moveOnKeyPress(a);
        object = a;
        if (settings.isAiEnabled())
            ai.play(object, mesh);
        nextObj = controller.makeShape();

        if (settings.isExternalPlayerEnabled()) {
            requestExternalMove();
        }

        Timer fall = new Timer();
        Button menuButton = new Button("Back to Menu");
        menuButton.setLayoutX(xMax + 5);
        // CHANGED: shifted down to make room for the difficulty label
        menuButton.setLayoutY(220);
        menuButton.setOnAction(e -> {
            fall.cancel();
            game = false;
            stopMusic();
            if (onGameOver != null)
                onGameOver.run();
        });
        groupe.getChildren().add(menuButton);
        final int tickMs = 30;
        final int[] elapsed = {0};
        TimerTask task = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        if (paused)
                            return;
                        elapsed[0] += tickMs;
                        if (elapsed[0] < (fastFall ? fastFallInterval : fallInterval))
                            return;
                        elapsed[0] = 0;
                        boolean atTop = object.a.getY() == 0 || object.b.getY() == 0 || object.c.getY() == 0
                                || object.d.getY() == 0;
                        if (atTop && isBlockedBelow(object))
                            top++;
                        else
                            top = 0;

                        if (top == 2) {
                            Text over = new Text("GAME OVER");
                            over.setFill(Color.RED);
                            over.setStyle("-fx-font: 70 arial;");
                            over.setY(250);
                            over.setX(10);
                            groupe.getChildren().add(over);

                            highScoreManager.updateHighScore(playerName, score);

                            game = false;
                        }
                        if (top == 15) {
                            fall.cancel();
                            if (onGameOver != null) {
                                onGameOver.run();
                            }
                        }

                        if (game) {
                            moveDown(object);
                            scoretext.setText("Score: " + Integer.toString(score));
                            level.setText("Lines: " + Integer.toString(linesNo));
                        }
                    }
                });
            }
        };
        fall.schedule(task, 0, tickMs);
    }

    private PureGame createPureGame() {

        int width = xMax / size;
        int height = yMax / size;

        int[][] cells = new int[height][width];

        // Convert mesh[x][y] into cells[y][x]
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[y][x] = mesh[x][y];
            }
        }

        int[][] currentShape = shapeToArray(object);
        int[][] nextShape = shapeToArray(nextObj);

        return new PureGame(
                width,
                height,
                cells,
                currentShape,
                nextShape
        );
    }

    private int[][] shapeToArray(form piece) {

        Rectangle[] blocks = {
                piece.a,
                piece.b,
                piece.c,
                piece.d
        };

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Rectangle block : blocks) {

            int x = (int) block.getX() / size;
            int y = (int) block.getY() / size;

            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        int[][] shape =
                new int[maxY - minY + 1][maxX - minX + 1];

        for (Rectangle block : blocks) {

            int x = (int) block.getX() / size - minX;
            int y = (int) block.getY() / size - minY;

            shape[y][x] = 1;
        }

        return shape;
    }

    private void requestExternalMove() {

        PureGame gameState = createPureGame();

        OpMove move = externalPlayer.requestMove(gameState);

        if (move == null) {

            System.out.println(
                    "External Player unavailable - piece will continue without external control."
            );

            showExternalWarning();

            return;
        }

        hideExternalWarning();

        System.out.println(
                "External move: X = " + move.opX()
                        + ", rotations = " + move.opRotate()
        );

        applyExternalMove(move);
    }

    private void applyExternalMove(OpMove move) {

        if (move == null) {
            return;
        }

        // Rotate the current piece
        for (int i = 0; i < move.opRotate(); i++) {
            MoveTurn(object);
        }

        // Find the current left-most X position
        int currentX = getPieceLeftX(object);

        // Move toward the server's target X position
        while (currentX < move.opX()) {

            int before = getPieceLeftX(object);

            controller.moveRight(object);

            int after = getPieceLeftX(object);

            // Movement failed, so stop to avoid an infinite loop
            if (before == after) {
                break;
            }

            currentX = after;
        }

        while (currentX > move.opX()) {

            int before = getPieceLeftX(object);

            controller.moveLeft(object);

            int after = getPieceLeftX(object);

            if (before == after) {
                break;
            }

            currentX = after;
        }

        System.out.println(
                "External player applied: target X = "
                        + move.opX()
                        + ", rotations = "
                        + move.opRotate()
        );
    }

    private void showExternalWarning() {

        if (externalWarningText != null) {
            externalWarningText.setVisible(true);
            externalWarningText.toFront();
        }
    }

    private void hideExternalWarning() {

        if (externalWarningText != null) {
            externalWarningText.setVisible(false);
        }
    }

    private int getPieceLeftX(form piece) {

        int aX = (int) piece.a.getX() / size;
        int bX = (int) piece.b.getX() / size;
        int cX = (int) piece.c.getX() / size;
        int dX = (int) piece.d.getX() / size;

        return Math.min(
                Math.min(aX, bX),
                Math.min(cX, dX)
        );
    }

    private void stopMusic() {
        if (audioManager != null) {
            audioManager.stopMusic();
        }
    }
    private void playMoveSound() {
        //System.out.println("MOVE SOUND CALLED");
        //System.out.println("settings = " + settings);
        //System.out.println("SFX enabled = " + settings.isSfxEnabled());
        audioManager.playMoveSound();
        //if (settings != null && settings.isSfxEnabled()) {
          //  audioManager.playMoveSound();
        //}
    }
    private void playClearSound() {
        if (settings != null && settings.isSfxEnabled()) {
            audioManager.playClearSound();
        }
    }

    private void moveOnKeyPress(form form) { //org.example.controller heavy movement of piece.
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

//                System.out.println(
//                        "KEY PRESSED: " + event.getCode()
//                                + " | AI = " + settings.isAiEnabled()
//                                + " | External = " + settings.isExternalPlayerEnabled()
//                );

                if (event.getCode() == KeyCode.P) {
                    paused = !paused;
                    pausedText.setVisible(paused);
                    return;
                }

                if (event.getCode() == KeyCode.M) {
                    boolean newMusicState = !settings.isMusicEnabled();

                    settings.setMusicEnabled(newMusicState);
                    audioManager.setMusicEnabled(newMusicState);

                    musicStatusText.setText(
                            "Music: " + (newMusicState ? "ON" : "OFF")
                    );
                    return;
                }

                if (event.getCode() == KeyCode.S) {
                    boolean newSfxState = !settings.isSfxEnabled();

                    settings.setSfxEnabled(newSfxState);
                    audioManager.setSfxEnabled(newSfxState);

                    sfxStatusText.setText(
                            "Sound: " + (newSfxState ? "ON" : "OFF")
                    );
                    return;
                }

                if (paused)
                    return;
                if (settings.isAiEnabled() || settings.isExternalPlayerEnabled())
                    return;
                switch (event.getCode()) {
                    case RIGHT:
                        controller.moveRight(form);
                            playMoveSound();

                        break;
                    case DOWN:
                        // hold to fall faster instead of slamming straight to the bottom
                        fastFall = true;
                        playMoveSound();
                        break;

                    case LEFT:
                        controller.moveLeft(form);
                            playMoveSound();
                        break;

                    case UP:
                        MoveTurn(form);
                        break;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN)
                    fastFall = false;
            }
        });
    }

    private void MoveTurn(form form) { //so the jist of this nightmare of comphrension is that each piece moves depending on it's placement to different area according to the sages this is what gives org.example.tetris it's neat juggling abilitiy.
        int f = form.form;
        Rectangle a = form.a;
        Rectangle b = form.b;
        Rectangle c = form.c;
        Rectangle d = form.d;
        switch (form.getName()) { //converted to enhanced switch case for marking, double check this if it doesn't work
            case "ll" -> {
                if (f == 1 && cB(a, 0, -2) && cB(b, 1, 1) && cB(d, -1, -1)) {
                    moveDown(form.a);
                    moveDown(form.a);
                    moveRight(form.b);
                    moveUp(form.b);
                    moveLeft(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 2 && cB(a, -2, 0) && cB(b, 1, -1) && cB(d, -1, 1)) {
                    moveLeft(form.a);
                    moveLeft(form.a);
                    moveRight(form.b);
                    moveDown(form.b);
                    moveLeft(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 3 && cB(a, 0, 2) && cB(b, -1, -1) && cB(d, 1, 1)) {
                    moveUp(form.a);
                    moveUp(form.a);
                    moveLeft(form.b);
                    moveDown(form.b);
                    moveRight(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 4 && cB(a, 2, 0) && cB(b, -1, 1) && cB(d, 1, -1)) {
                    moveRight(form.a);
                    moveRight(form.a);
                    moveLeft(form.b);
                    moveUp(form.b);
                    moveRight(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }

            }
            case "l" -> {
                if (f == 1 && cB(a, 2, 0) && cB(b, 1, 1) && cB(d, -1, -1)) {
                    moveRight(form.a);
                    moveRight(form.a);
                    moveRight(form.b);
                    moveUp(form.b);
                    moveLeft(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 2 && cB(a, 0, -2) && cB(b, 1, -1) && cB(d, -1, 1)) {
                    moveDown(form.a);
                    moveDown(form.a);
                    moveRight(form.b);
                    moveDown(form.b);
                    moveLeft(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 3 && cB(a, -2, 0) && cB(b, -1, -1) && cB(d, 1, 1)) {
                    moveLeft(form.a);
                    moveLeft(form.a);
                    moveLeft(form.b);
                    moveDown(form.b);
                    moveRight(form.d);
                    moveUp(form.d);
                    form.changeForm();
                    break;
                }
                if (f == 4 && cB(a, 0, 2) && cB(b, -1, 1) && cB(d, 1, -1)) {
                    moveUp(form.a);
                    moveUp(form.a);
                    moveLeft(form.b);
                    moveUp(form.b);
                    moveRight(form.d);
                    moveDown(form.d);
                    form.changeForm();
                    break;
                }
            }
            case "square" -> {
            }
            case "s" -> {
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
            }
            case "t" -> {
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
            }
            case "zig" -> {
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
                }
            }
            case "line" -> {
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
    }

    private void moveDown(Rectangle rect) {
        if (rect.getY() + move < yMax)
            rect.setY(rect.getY() + move);

    }

    private void moveDown(form form) {
        if (isBlockedBelow(form)) {
            mesh[(int) form.a.getX() / size][(int) form.a.getY() / size] = 1;
            mesh[(int) form.b.getX() / size][(int) form.b.getY() / size] = 1;
            mesh[(int) form.c.getX() / size][(int) form.c.getY() / size] = 1;
            mesh[(int) form.d.getX() / size][(int) form.d.getY() / size] = 1;
            RemoveRows(groupe);

            form a = nextObj;
            nextObj = controller.makeShape();
            object = a;
            if (settings.isAiEnabled())
                ai.play(object, mesh);

            if (settings.isExternalPlayerEnabled()) {

//                System.out.println(
//                        "NEW PIECE CREATED - asking external player"
//                );

                requestExternalMove();
            }

            groupe.getChildren().addAll(a.a, a.b, a.c, a.d);
            moveOnKeyPress(a);
        } else {
            form.a.setY(form.a.getY() + move);
            form.b.setY(form.b.getY() + move);
            form.c.setY(form.c.getY() + move);
            form.d.setY(form.d.getY() + move);
        }
    }

    private boolean isBlockedBelow(form form) {
        return form.a.getY() == yMax - size || form.b.getY() == yMax - size || form.c.getY() == yMax - size
                || form.d.getY() == yMax - size || moveA(form) || moveB(form) || moveC(form) || moveD(form);
    }

    private void moveRight(Rectangle rect) {
        if (rect.getX() + move <= xMax - size)
            rect.setX(rect.getX() + move);
        playMoveSound();
    }

    private void moveLeft(Rectangle rect) {
        if (rect.getX() - move >= 0)
            rect.setX(rect.getX() - move);
        playMoveSound();
    }

    private void moveUp(Rectangle rect) {
        if (rect.getY() - move >= 0)
            rect.setY(rect.getY() - move);
    }

    private boolean cB(Rectangle rect, int x, int y) {
        boolean xb = false;
        boolean yb = false;
        if (x >= 0)
            xb = rect.getX() + x * move <= xMax - size;
        if (x < 0)
            xb = rect.getX() + x * move >= 0;
        if (y > 0)
            yb = rect.getY() - y * move >= 0;
        else if (y < 0)
            yb = rect.getY() - y * move < yMax;
        else
            yb = true;
        return xb && yb && mesh[((int) rect.getX() / size) + x][((int) rect.getY() / size) - y] == 0;
    }

    private void RemoveRows(Pane pane) {
        ArrayList<Node> rects = new ArrayList<Node>();
        ArrayList<Integer> lines = new ArrayList<Integer>();
        ArrayList<Node> newrects = new ArrayList<Node>();
        int full = 0;
        for (int i = 0; i < mesh[0].length; i++) {
            for (int[] ints : mesh) {
                if (ints[i] == 1)
                    full++;
            }
            if (full == mesh.length)
                lines.add(i);
            full = 0;
        }
        if (!lines.isEmpty()){
            audioManager.playClearSound();

            do {
                for (Node node : pane.getChildren()) {
                    if (node instanceof Rectangle)
                        rects.add(node);
                }
                score += 50;
                linesNo++;

                for (Node node : rects) {
                    Rectangle a = (Rectangle) node;
                    if (a.getY() == lines.get(0) * size) {
                        mesh[(int) a.getX() / size][(int) a.getY() / size] = 0;
                        pane.getChildren().remove(node);
                    } else
                        newrects.add(node);
                }
                for (Node node : newrects) {
                    Rectangle a = (Rectangle) node;
                    if (a.getY() < lines.get(0) * size) {
                        mesh[(int) a.getX() / size][(int) a.getY() / size] = 0;
                        a.setY(a.getY() + size);
                    }
                }
                lines.remove(0);
                rects.clear();
                newrects.clear();
                for (Node node : pane.getChildren()) {
                    if (node instanceof Rectangle)
                        rects.add(node);
                }
                for (Node node : rects) {
                    Rectangle a = (Rectangle) node;
                    try {
                        mesh[(int) a.getX() / size][(int) a.getY() / size] = 1;
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
                rects.clear();
            }
            while (!lines.isEmpty());
    }}
    private boolean moveA(form form) {
        return (mesh[(int) form.a.getX() / size][((int) form.a.getY() / size) + 1] == 1);
    }

    private boolean moveB(form form) {
        return (mesh[(int) form.b.getX() / size][((int) form.b.getY() / size) + 1] == 1);
    }

    private boolean moveC(form form) {
        return (mesh[(int) form.c.getX() / size][((int) form.c.getY() / size) + 1] == 1);
    }

    private boolean moveD(form form) {
        return (mesh[(int) form.d.getX() / size][((int) form.d.getY() / size) + 1] == 1);
    }

    }
