package org.example;

import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

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

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("tetrominoOffsets")
    void makeShapeCentersEachTetrominoOnGivenBoardWidth(String name, int[] xOffsets, int[] yOffsets) {
        form piece = controller.makeShape(name, Tetris.xMax);
        int center = (Tetris.xMax / controller.size / 2) * controller.size;

        assertEquals(name, piece.getName());
        assertEquals(center + xOffsets[0] * controller.size, piece.a.getX());
        assertEquals(center + xOffsets[1] * controller.size, piece.b.getX());
        assertEquals(center + xOffsets[2] * controller.size, piece.c.getX());
        assertEquals(center + xOffsets[3] * controller.size, piece.d.getX());
        assertEquals(yOffsets[0] * controller.size, piece.a.getY());
        assertEquals(yOffsets[1] * controller.size, piece.b.getY());
        assertEquals(yOffsets[2] * controller.size, piece.c.getY());
        assertEquals(yOffsets[3] * controller.size, piece.d.getY());
    }

    static Stream<Arguments> tetrominoOffsets() {
        // offsets are in units of controller.size, relative to the board's horizontal center
        return Stream.of(
                Arguments.of("l", new int[]{-1, -1, 0, 1}, new int[]{0, 1, 1, 1}),
                Arguments.of("ll", new int[]{1, -1, 0, 1}, new int[]{0, 1, 1, 1}),
                Arguments.of("square", new int[]{-1, 0, -1, 0}, new int[]{0, 0, 1, 1}),
                Arguments.of("s", new int[]{1, 0, 0, -1}, new int[]{0, 0, 1, 1}),
                Arguments.of("zig", new int[]{1, 0, 1, 2}, new int[]{0, 0, 1, 1}),
                Arguments.of("t", new int[]{-1, 0, 0, 1}, new int[]{0, 0, 1, 0}),
                Arguments.of("line", new int[]{-2, -1, 0, 1}, new int[]{0, 0, 0, 0})
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"hexagon", "pentagon", "circle", "", "SQUARE"})
    void makeShapeRejectsUnknownTetrominoNames(String name) {
        assertThrows(
                IllegalArgumentException.class,
                () -> controller.makeShape(name, Tetris.xMax)
        );
    }

    @Test
    void makeShapeWithoutNameProducesAKnownRandomTetromino() {
        Set<String> validNames = Set.of("l", "ll", "square", "s", "zig", "t", "line");

        form piece = controller.makeShape();

        assertTrue(validNames.contains(piece.getName()));
    }
}