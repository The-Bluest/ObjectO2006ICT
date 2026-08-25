package org.example;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
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
    public static final int move = 25; //Settings dependent
    public static final int size = 25;// settings dependent if movable by 1 value
    public static int xMax = 250; //settings dependent if deliberately altered
    public static int yMax = 500;// settings dependent if deliberately altered
    public static int[][] mesh = new int[xMax / size][yMax / size];
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
    private Runnable onGameOver;



    public void start(StackPane root, Runnable onGameOver) throws Exception {
        this.onGameOver = onGameOver;
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
        pausedText = new Text("PAUSED");
        pausedText.setFill(Color.RED);
        pausedText.setStyle("-fx-font: 40 arial;");
        pausedText.setY(250);
        pausedText.setX(10);
        pausedText.setVisible(false);
        groupe.getChildren().addAll(scoretext, line, level, pausedText);

        form a = nextObj;
        groupe.getChildren().addAll(a.a, a.b, a.c, a.d);
        moveOnKeyPress(a);
        object = a;
        nextObj = controller.makeShape();

        Timer fall = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        if (paused)
                            return;
                        boolean atTop = object.a.getY() == 0 || object.b.getY() == 0 || object.c.getY() == 0
                                || object.d.getY() == 0;
                        // only a genuinely stuck piece (blocked right at spawn) counts toward game over
                        if (atTop && isBlockedBelow(object))
                            top++;
                        else
                            top = 0;

                        if (top == 2) {
                            // GAME OVER
                            Text over = new Text("GAME OVER");
                            over.setFill(Color.RED);
                            over.setStyle("-fx-font: 70 arial;");
                            over.setY(250);
                            over.setX(10);
                            groupe.getChildren().add(over);
                            game = false;
                        }
                        // return to the main menu after the player has seen the GAME OVER text
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
        fall.schedule(task, 0, 300);
    }


    private void moveOnKeyPress(form form) { //org.example.controller heavy movement of piece.
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.P) {
                    paused = !paused;
                    pausedText.setVisible(paused);
                    return;
                }
                if (paused)
                    return;
                switch (event.getCode()) {
                    case RIGHT:
                        controller.moveRight(form);
                        break;
                    case DOWN:
                        // slam the piece straight down and lock it in immediately
                        while (!isBlockedBelow(form)) {
                            moveDown(form);
                            score++;
                        }
                        moveDown(form);
                        break;
                    case LEFT:
                        controller.moveLeft(form);
                        break;
                    case UP:
                        MoveTurn(form);
                        break;
                }
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
    }

    private void moveLeft(Rectangle rect) {
        if (rect.getX() - move >= 0)
            rect.setX(rect.getX() - move);
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
        if (!lines.isEmpty())
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
    }
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
