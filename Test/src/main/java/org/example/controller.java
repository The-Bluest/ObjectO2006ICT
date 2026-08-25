package org.example;

import javafx.scene.shape.Rectangle;


public class controller {
    // get from org.example.tetris for control
    public static final int move = Tetris.move;
    public static final int size = Tetris.size;
    public static int xMax = Tetris.xMax;
    public static int yMax = Tetris.yMax;
    public static int[][] mesh = Tetris.mesh;

    public static void moveRight(form form) {
        if (form.a.getX() + move <= xMax - size && form.b.getX() + move <= xMax - size && form.c.getX() + move <= xMax - size && form.d.getX() + move <= xMax - size) {
            int movea = mesh[((int) form.a.getX() / size) + 1][((int) form.a.getY() / size)];
            int moveb = mesh[((int) form.b.getX() / size) + 1][((int) form.b.getY() / size)];
            int movec = mesh[((int) form.c.getX() / size) + 1][((int) form.c.getY() / size)];
            int moved = mesh[((int) form.d.getX() / size) + 1][((int) form.d.getY() / size)];
            if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
                form.a.setX(form.a.getX() + move);
                form.b.setX(form.b.getX() + move);
                form.c.setX(form.c.getX() + move);
                form.d.setX(form.d.getX() + move);
            }
        }
    }

    public static void moveLeft(form form) {
        if (form.a.getX() - move >= 0 && form.b.getX() - move >= 0 && form.c.getX() - move >= 0 && form.d.getX() - move >= 0) {
            int movea = mesh[((int) form.a.getX() / size) - 1][((int) form.a.getY() / size)];
            int moveb = mesh[((int) form.b.getX() / size) - 1][((int) form.b.getY() / size)];
            int movec = mesh[((int) form.c.getX() / size) - 1][((int) form.c.getY() / size)];
            int moved = mesh[((int) form.d.getX() / size) - 1][((int) form.d.getY() / size)];
            if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
                form.a.setX(form.a.getX() - move);
                form.b.setX(form.b.getX() - move);
                form.c.setX(form.c.getX() - move);
                form.d.setX(form.d.getX() - move);
            }
        }
    }

    //actually make the shapes
    //basicilly manually built each blocks instructions, then run for random to decide which one it makes 
    public static form makeShape() {
        int block = (int) (Math.random() * 100); //what even is random
        String name;
        Rectangle a = new Rectangle(size - 1, size - 1), b = new Rectangle(size - 1, size - 1), c = new Rectangle(size - 1, size - 1), d = new Rectangle(size - 1, size - 1);
        if (block < 15) { //makes orange L %15
            a.setX(xMax / 2 - size);
            b.setX(xMax / 2 - size);
            b.setY(size);
            c.setX(xMax / 2);
            c.setY(size);
            d.setX(xMax / 2 + size);
            d.setY(size);
            name = "l";

        } else if (block < 30) { //Makes Blue L 15%
            a.setX(xMax / 2 + size);
            b.setX(xMax / 2 - size);
            b.setY(size);
            c.setX(xMax / 2);
            c.setY(size);
            d.setX(xMax / 2 + size);
            d.setY(size);
            name = "ll";

        } else if (block < 45) { //square 15%
            a.setX(xMax / 2 - size);
            b.setX(xMax / 2);
            c.setX(xMax / 2 - size);
            c.setY(size);
            d.setX(xMax / 2);
            d.setY(size);
            name = "square";

        } else if (block < 60) { //makes zag 15%
            a.setX(xMax / 2 + size);
            b.setX(xMax / 2);
            c.setX(xMax / 2);
            c.setY(size);
            d.setX(xMax / 2 - size);
            d.setY(size);
            name = "s";

        } else if (block < 70) { //makes zig 15%
            a.setX(xMax / 2 + size);
            b.setX(xMax / 2);
            c.setX(xMax / 2 + size);
            c.setY(size);
            d.setX(xMax / 2 + size + size);
            d.setY(size);
            name = "zig";
        } else if (block < 85) { //makes t 15%
            a.setX(xMax / 2 - size);
            b.setX(xMax / 2);
            c.setX(xMax / 2);
            c.setY(size);
            d.setX(xMax / 2 + size);
            name = "t";
        } else {  //CREATES THE HOLY LINE PIECE, CHOOSEN SAVIOR.
            a.setX(xMax / 2 - size - size);
            b.setX(xMax / 2 - size);
            c.setX(xMax / 2);
            d.setX(xMax / 2 + size);
            name = "line";
        }
        return new form(a, b, c, d, name); //woa shape be upon thee
    }

}
