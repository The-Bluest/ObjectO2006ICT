package org.example;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

public class PlayerBoard {
    private static final int SIZE = Tetris.size;
    private static final int MOVE = Tetris.move;
    private final int xMax;
    private final int yMax;
    private final int[][] mesh;
    private final Pane gamePane;
    private final VBox view;
    private final Label playerLabel;
    private final Label scoreLabel;
    private final Label linesLabel;
    private final Label pieceLabel;
    private final PlayerType playerType;
    private final SharedPieceSequence sharedSequence;
    private final AI ai;
    private form currentPiece;
    private form nextPiece;
    private int pieceIndex = 0;
    private int score = 0;
    private int lines = 0;
    private boolean gameOver = false;
    private int elapsed = 0;
    private final double fallInterval;

    public PlayerBoard(
            String playerName,
            PlayerType playerType,
            SharedPieceSequence sharedSequence,
            Settings settings
    ) {
        this.playerType = playerType;
        this.sharedSequence = sharedSequence;
        this.xMax = settings.getGameWidth() * SIZE;
        this.yMax = settings.getGameHeight() * SIZE;
        this.mesh = new int[
                settings.getGameWidth()
                ][
                settings.getGameHeight()
                ];
        this.fallInterval =
                Math.max(
                        100,
                        1000 - settings.getGameSpeed() * 100
                );
        this.ai = new AI(new boardeval());
        gamePane = new Pane();
        gamePane.setPrefSize(xMax, yMax);
        gamePane.setMinSize(xMax, yMax);
        gamePane.setMaxSize(xMax, yMax);
        gamePane.setStyle(
                "-fx-border-color: black;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );
        playerLabel = new Label(
                playerName + " - " + playerType
        );
        playerLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );
        scoreLabel = new Label("Score: 0");
        linesLabel = new Label("Lines: 0");
        pieceLabel = new Label("Piece: -");
        view = new VBox(
                5,
                playerLabel,
                scoreLabel,
                linesLabel,
                pieceLabel,
                gamePane
        );
        view.setAlignment(Pos.TOP_CENTER);
        Line rightBorder =
                new Line(
                        xMax - 1,
                        0,
                        xMax - 1,
                        yMax
                );

        gamePane.getChildren().add(rightBorder);
        createFirstPiece();
    }

    private void createFirstPiece() {
        String currentType =
                sharedSequence.getPiece(pieceIndex);
        String nextType =
                sharedSequence.getPiece(pieceIndex + 1);
        currentPiece =
                controller.makeShape(
                        currentType,
                        xMax
                );
        nextPiece =
                controller.makeShape(
                        nextType,
                        xMax
                );
        addPieceToBoard(currentPiece);
        pieceLabel.setText(
                "Piece #" +
                        pieceIndex +
                        ": " +
                        currentType +
                        " | Next: " +
                        nextType
        );
        preparePlayerMove();
    }

    public void tick(int tickMs) {
        if (gameOver) {
            return;
        }
        elapsed += tickMs;
        if (elapsed >= fallInterval) {
            elapsed = 0;
            moveDown();
        }
    }

    private void moveDown() {
        if (currentPiece == null || gameOver) {
            return;
        }
        if (isBlockedBelow(currentPiece)) {
            lockCurrentPiece();
            removeRows();
            spawnNextPiece();
        } else {
            currentPiece.a.setY(
                    currentPiece.a.getY() + MOVE
            );
            currentPiece.b.setY(
                    currentPiece.b.getY() + MOVE
            );
            currentPiece.c.setY(
                    currentPiece.c.getY() + MOVE
            );
            currentPiece.d.setY(
                    currentPiece.d.getY() + MOVE
            );
        }
    }

    private boolean isBlockedBelow(form piece) {
        return blockBelow(piece.a)
                || blockBelow(piece.b)
                || blockBelow(piece.c)
                || blockBelow(piece.d);
    }

    private boolean blockBelow(
            javafx.scene.shape.Rectangle block
    ) {
        int x =
                (int) block.getX() / SIZE;
        int y =
                (int) block.getY() / SIZE;
        if (y >= mesh[0].length - 1) {
            return true;
        }
        return mesh[x][y + 1] == 1;
    }


    private void lockCurrentPiece() {
        lockBlock(currentPiece.a);
        lockBlock(currentPiece.b);
        lockBlock(currentPiece.c);
        lockBlock(currentPiece.d);
    }

    private void lockBlock(
            javafx.scene.shape.Rectangle block
    ) {
        int x =
                (int) block.getX() / SIZE;
        int y =
                (int) block.getY() / SIZE;
        if (
                x >= 0 &&
                        x < mesh.length &&
                        y >= 0 &&
                        y < mesh[0].length
        ) {
            mesh[x][y] = 1;
        }
    }

    private void spawnNextPiece() {
        pieceIndex++;
        currentPiece = nextPiece;
        String nextType =
                sharedSequence.getPiece(
                        pieceIndex + 1
                );
        nextPiece =
                controller.makeShape(
                        nextType,
                        xMax
                );
        if (spawnPositionBlocked(currentPiece)) {
            gameOver = true;
            playerLabel.setText(
                    playerLabel.getText()
                            + " - GAME OVER"
            );
            return;
        }
        addPieceToBoard(currentPiece);
        pieceLabel.setText(
                "Piece #" +
                        pieceIndex +
                        ": " +
                        currentPiece.getName() +
                        " | Next: " +
                        nextType
        );
        preparePlayerMove();
    }

    private void preparePlayerMove() {
        if (playerType == PlayerType.AI) {
            ai.play(
                    currentPiece,
                    mesh
            );
        }

    }

    private boolean spawnPositionBlocked(form piece) {
        return positionOccupied(piece.a)
                || positionOccupied(piece.b)
                || positionOccupied(piece.c)
                || positionOccupied(piece.d);
    }

    private boolean positionOccupied(
            javafx.scene.shape.Rectangle block
    ) {
        int x =
                (int) block.getX() / SIZE;
        int y =
                (int) block.getY() / SIZE;
        if (
                x < 0 ||
                        x >= mesh.length ||
                        y < 0 ||
                        y >= mesh[0].length
        ) {
            return true;
        }
        return mesh[x][y] == 1;
    }

    private void addPieceToBoard(form piece) {
        gamePane.getChildren().addAll(
                piece.a,
                piece.b,
                piece.c,
                piece.d
        );
    }

    private void removeRows() {
        int width = mesh.length;
        int height = mesh[0].length;
        for (int y = height - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < width; x++) {
                if (mesh[x][y] == 0) {

                    full = false;
                    break;
                }
            }
            if (full) {
                removeRowFromPane(y);
                for (int row = y; row > 0; row--) {
                    for (
                            int x = 0;
                            x < width;
                            x++
                    ) {
                        mesh[x][row] =
                                mesh[x][row - 1];
                    }
                }
                for (int x = 0; x < width; x++) {
                    mesh[x][0] = 0;
                }
                moveBlocksAboveDown(y);
                score += 100;
                lines++;

                scoreLabel.setText(
                        "Score: " + score
                );
                linesLabel.setText(
                        "Lines: " + lines
                );
                y++;
            }
        }
    }


    private void removeRowFromPane(int row) {
        double rowY = row * SIZE;
        gamePane.getChildren().removeIf(
                node ->
                        node instanceof
                                javafx.scene.shape.Rectangle
                                &&
                                ((javafx.scene.shape.Rectangle) node)
                                        .getY()
                                        == rowY
        );
    }

    private void moveBlocksAboveDown(int row) {
        double rowY = row * SIZE;
        for (
                javafx.scene.Node node :
                gamePane.getChildren()
        ) {
            if (
                    node instanceof
                            javafx.scene.shape.Rectangle
            ) {
                javafx.scene.shape.Rectangle rect =
                        (javafx.scene.shape.Rectangle) node;
                if (rect.getY() < rowY) {
                    rect.setY(
                            rect.getY() + SIZE
                    );
                }
            }
        }
    }


    public VBox getView() {
        return view;
    }

    public form getCurrentPiece() {
        return currentPiece;
    }

    public int[][] getMesh() {
        return mesh;
    }

    public int getXMax() {
        return xMax;
    }

    public int getYMax() {
        return yMax;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public int getPieceIndex() {
        return pieceIndex;
    }
}