package org.example;

import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class controllerTest {

    @BeforeEach
    void setUp() {
        Tetris.xMax = 8 * controller.size;
        Tetris.mesh = new int[(Tetris.xMax / controller.size) + 2][20];
    }

    @Test
    void moveRightShiftsAllCornersWhenPathIsClear() {
        form piece = controller.makeShape("square", Tetris.xMax);
        double startAX = piece.a.getX();
        double startBX = piece.b.getX();
        double startCX = piece.c.getX();
        double startDX = piece.d.getX();

        assertTrue(controller.moveRight(piece));

        assertEquals(startAX + controller.move, piece.a.getX());
        assertEquals(startBX + controller.move, piece.b.getX());
        assertEquals(startCX + controller.move, piece.c.getX());
        assertEquals(startDX + controller.move, piece.d.getX());
    }

    @Test
    void moveRightReturnsFalseWhenBlockedByMesh() {
        form piece = controller.makeShape("square", Tetris.xMax);
        double startAX = piece.a.getX();
        int blockedCol = ((int) piece.a.getX() / controller.size) + 1;
        int blockedRow = (int) piece.a.getY() / controller.size;
        Tetris.mesh[blockedCol][blockedRow] = 1;

        assertFalse(controller.moveRight(piece));
        assertEquals(startAX, piece.a.getX());
    }

    @Test
    void moveRightReturnsFalseAtRightEdgeWithoutTouchingMesh() {
        Rectangle a = new Rectangle(controller.size - 1, controller.size - 1);
        a.setX(Tetris.xMax - controller.size);
        Rectangle b = new Rectangle(controller.size - 1, controller.size - 1);
        Rectangle c = new Rectangle(controller.size - 1, controller.size - 1);
        Rectangle d = new Rectangle(controller.size - 1, controller.size - 1);
        form piece = new form(a, b, c, d, "square");

        assertFalse(controller.moveRight(piece));
    }

    @Test
    void moveLeftShiftsAllCornersWhenPathIsClear() {
        form piece = controller.makeShape("square", Tetris.xMax);
        double startAX = piece.a.getX();
        double startBX = piece.b.getX();
        double startCX = piece.c.getX();
        double startDX = piece.d.getX();

        assertTrue(controller.moveLeft(piece));

        assertEquals(startAX - controller.move, piece.a.getX());
        assertEquals(startBX - controller.move, piece.b.getX());
        assertEquals(startCX - controller.move, piece.c.getX());
        assertEquals(startDX - controller.move, piece.d.getX());
    }

    @Test
    void moveLeftReturnsFalseAtLeftEdge() {
        Rectangle a = new Rectangle(controller.size - 1, controller.size - 1);
        Rectangle b = new Rectangle(controller.size - 1, controller.size - 1);
        Rectangle c = new Rectangle(controller.size - 1, controller.size - 1);
        Rectangle d = new Rectangle(controller.size - 1, controller.size - 1);
        form piece = new form(a, b, c, d, "square");

        assertFalse(controller.moveLeft(piece));
    }

    @Test
    void makeShapeCentersPieceOnGivenBoardWidth() {
        form piece = controller.makeShape("line", 8 * controller.size);

        assertEquals("line", piece.getName());
        int center = (8 / 2) * controller.size;
        assertEquals(center - 2 * controller.size, piece.a.getX());
        assertEquals(center - controller.size, piece.b.getX());
        assertEquals(center, piece.c.getX());
        assertEquals(center + controller.size, piece.d.getX());
    }

    @Test
    void makeShapeRejectsUnknownTetrominoName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> controller.makeShape("hexagon", Tetris.xMax)
        );
    }

    @Test
    void makeShapeWithoutNameProducesAKnownRandomTetromino() {
        Set<String> validNames = Set.of("l", "ll", "square", "s", "zig", "t", "line");

        form piece = controller.makeShape();

        assertTrue(validNames.contains(piece.getName()));
    }
}