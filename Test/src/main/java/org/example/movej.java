package org.example;

import javafx.scene.shape.Rectangle;

//probably redundant, just merged it into org.example.tetris.java
public class movej {
    public static final int move = main.move;
    public static final int size = main.size;
    public static int xMax = main.xMax;
    public static int yMax = main.yMax;
    public static int[][] mesh = main.mesh;

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