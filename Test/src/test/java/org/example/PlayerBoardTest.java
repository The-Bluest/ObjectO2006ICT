package org.example;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerBoardTest {

    @BeforeAll
    static void initJavaFxToolkit() {
        // PlayerBoard builds Label/Button controls, which need the FX toolkit running even when nothing is shown
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException alreadyRunning) {
            // another test class in this JVM already started the toolkit
        }
    }

    @Test
    void createFirstPieceUsesTheStubbedSequenceInsteadOfRandomPieces() {
        PlayerBoard board = new PlayerBoard(
                "P1", PlayerType.HUMAN, new StubSharedPieceSequence("square"), new Settings()
        );

        assertEquals("square", board.getCurrentPiece().getName());
    }

    @Test
    void handleInputMovesTheHumanPieceRight() {
        PlayerBoard board = new PlayerBoard(
                "P1", PlayerType.HUMAN, new StubSharedPieceSequence("square"), new Settings()
        );
        double xBefore = board.getCurrentPiece().a.getX();

        board.handleInput(PlayerBoard.Action.RIGHT);

        assertEquals(xBefore + Tetris.size, board.getCurrentPiece().a.getX());
    }

    @Test
    void handleInputIsIgnoredForNonHumanPlayers() {
        PlayerBoard board = new PlayerBoard(
                "P1", PlayerType.AI, new StubSharedPieceSequence("square"), new Settings()
        );
        form piece = board.getCurrentPiece();
        double xBefore = piece.a.getX();
        double yBefore = piece.a.getY();

        board.handleInput(PlayerBoard.Action.LEFT);

        assertEquals(xBefore, piece.a.getX());
        assertEquals(yBefore, piece.a.getY());
    }
}
