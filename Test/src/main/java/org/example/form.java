package org.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class form {
    // each part of the shape 
    Rectangle a;
    Rectangle b;
    Rectangle c;
    Rectangle d;
    Color color;
    private String name;
    public int form = 1;

    //shape instructions
    public form(Rectangle a, Rectangle b, Rectangle c, Rectangle d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public form(Rectangle a, Rectangle b, Rectangle c, Rectangle d, String name) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.name = name;

        //switch instead of ifelse and establishing the type colours
        switch (name) {
            case "ll":
                color = Color.BLUE;
                break;
            case "l":
                color = Color.ORANGE;
                break;
            case "square":
                color = Color.YELLOW;
                break;
            case "zig":
                color = Color.GREEN;
                break;
            case "t":
                color = Color.PINK;
                break;
            case "zag":
                color = Color.RED;
                break;
            case "line":
                color = Color.AQUA;
                break;
        }
        //applies the above colours to each dimension.
        this.a.setFill(color);
        this.b.setFill(color);
        this.c.setFill(color);
        this.d.setFill(color);

    }

    public String getName() {
        return name;
    }

    public void changeForm() {
        if (form != 4) {
            form++;
        } else {
            form = 1;
        }
    }
}
