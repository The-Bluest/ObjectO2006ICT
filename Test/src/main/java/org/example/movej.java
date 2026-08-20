package org.example;

import javafx.scene.shape.Rectangle;

//probably redundant, just merged it into org.example.tetris.java
public class movej {
    public static final int move = Tetris.move;
    public static final int size = Tetris.size;
    public static int xMax = Tetris.xMax;
    public static int yMax = Tetris.yMax;
    public static int[][] mesh = Tetris.mesh;

    private void Down(Rectangle rect) {
        if (rect.getY() + move < yMax)
            rect.setY(rect.getY() + move);

    }

    public void Right(Rectangle rect) {
        if (rect.getX() + move <= xMax - size)
            rect.setX(rect.getX() + move);
    }

    public void Left(Rectangle rect) {
        if (rect.getX() - move >= 0)
            rect.setX(rect.getX() - move);
    }
}