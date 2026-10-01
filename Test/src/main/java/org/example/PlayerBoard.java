package org.example;

import javafx.application.Platform;
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
    private final Label difficultyLabel; // add
    private final PlayerType playerType;
    private final SharedPieceSequence sharedSequence;
    private final AI ai;
    private final ExternalPlayer externalPlayer;
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
        this.ai = new AI(boardeval.getInstance());
        this.externalPlayer = new ExternalPlayer();
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
        // add: difficulty label for the board
        difficultyLabel = new Label(
                "Difficulty: " + settings.getDifficulty()
        );
        difficultyLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: darkblue;"
        );
        view = new VBox(
                5,
                playerLabel,
                // add: difficulty shown under the player name
                difficultyLabel,
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
        } else if (playerType == PlayerType.EXTERNAL) {
            requestExternalMove();
        }

    }

    // sends a snapshot to the external server on a background thread so the shared game loop never blocks on it
    private void requestExternalMove() {
        if (currentPiece == null) {
            return;
        }
        form requestedPiece = currentPiece;
        PureGame snapshot = createPureGame();
        Thread requestThread = new Thread(() -> {
            OpMove move = externalPlayer.requestMove(snapshot);
            Platform.runLater(
                    () -> applyExternalMove(move, requestedPiece)
            );
        });
        requestThread.setDaemon(true);
        requestThread.start();
    }

    // discards the response if the piece it was computed for is no longer current (already locked/replaced)
    private void applyExternalMove(OpMove move, form requestedPiece) {
        if (move == null || gameOver || currentPiece != requestedPiece) {
            return;
        }
        for (int i = 0; i < move.opRotate(); i++) {
            rotate();
        }
        int currentX = leftColumn(currentPiece);
        while (currentX < move.opX()) {
            moveHorizontal(1);
            int after = leftColumn(currentPiece);
            if (after == currentX) {
                break;
            }
            currentX = after;
        }
        while (currentX > move.opX()) {
            moveHorizontal(-1);
            int after = leftColumn(currentPiece);
            if (after == currentX) {
                break;
            }
            currentX = after;
        }
    }

    private int leftColumn(form piece) {
        return Math.min(
                Math.min(gridX(piece.a), gridX(piece.b)),
                Math.min(gridX(piece.c), gridX(piece.d))
        );
    }

    private PureGame createPureGame() {
        int width = mesh.length;
        int height = mesh[0].length;
        int[][] cells = new int[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[y][x] = mesh[x][y];
            }
        }
        return new PureGame(
                width,
                height,
                cells,
                shapeToArray(currentPiece),
                shapeToArray(nextPiece)
        );
    }

    private int[][] shapeToArray(form piece) {
        javafx.scene.shape.Rectangle[] blocks = {
                piece.a, piece.b, piece.c, piece.d
        };
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (javafx.scene.shape.Rectangle block : blocks) {
            minX = Math.min(minX, gridX(block));
            maxX = Math.max(maxX, gridX(block));
            minY = Math.min(minY, gridY(block));
            maxY = Math.max(maxY, gridY(block));
        }
        int[][] shape = new int[maxY - minY + 1][maxX - minX + 1];
        for (javafx.scene.shape.Rectangle block : blocks) {
            shape[gridY(block) - minY][gridX(block) - minX] = 1;
        }
        return shape;
    }

    public enum Action {
        LEFT,
        RIGHT,
        DOWN,
        ROTATE
    }

    // entry point for human keyboard controls, routed in from TwoPlayerGame
    public void handleInput(Action action) {
        if (
                playerType != PlayerType.HUMAN ||
                        gameOver ||
                        currentPiece == null
        ) {
            return;
        }
        switch (action) {
            case LEFT -> moveHorizontal(-1);
            case RIGHT -> moveHorizontal(1);
            case DOWN -> {
                moveDown();
                elapsed = 0;
            }
            case ROTATE -> rotate();
        }
    }

    private void moveHorizontal(int dCol) {
        if (canMoveTo(currentPiece, dCol, 0)) {
            shiftPiece(currentPiece, dCol, 0);
        }
    }

    private void rotate() {
        int[][][] states = AI.shapeStates(currentPiece.getName());
        if (states == null) {
            return;
        }
        int anchorCol = Math.min(
                Math.min(gridX(currentPiece.a), gridX(currentPiece.b)),
                Math.min(gridX(currentPiece.c), gridX(currentPiece.d))
        );
        int anchorRow = Math.min(
                Math.min(gridY(currentPiece.a), gridY(currentPiece.b)),
                Math.min(gridY(currentPiece.c), gridY(currentPiece.d))
        );
        int nextRotation = currentPiece.form % states.length;
        int[][] cells = states[nextRotation];
        if (!cellsFit(cells, anchorCol, anchorRow)) {
            return;
        }
        javafx.scene.shape.Rectangle[] blocks = {
                currentPiece.a, currentPiece.b, currentPiece.c, currentPiece.d
        };
        for (int i = 0; i < blocks.length; i++) {
            blocks[i].setX((anchorCol + cells[i][0]) * SIZE);
            blocks[i].setY((anchorRow + cells[i][1]) * SIZE);
        }
        currentPiece.form = nextRotation + 1;
    }

    private boolean cellsFit(int[][] cells, int anchorCol, int anchorRow) {
        for (int[] cell : cells) {
            int x = anchorCol + cell[0];
            int y = anchorRow + cell[1];
            if (
                    x < 0 || x >= mesh.length ||
                            y < 0 || y >= mesh[0].length ||
                            mesh[x][y] == 1
            ) {
                return false;
            }
        }
        return true;
    }

    private boolean canMoveTo(form piece, int dCol, int dRow) {
        return canPlace(piece.a, dCol, dRow)
                && canPlace(piece.b, dCol, dRow)
                && canPlace(piece.c, dCol, dRow)
                && canPlace(piece.d, dCol, dRow);
    }

    private boolean canPlace(
            javafx.scene.shape.Rectangle block,
            int dCol,
            int dRow
    ) {
        int x = gridX(block) + dCol;
        int y = gridY(block) + dRow;
        if (x < 0 || x >= mesh.length || y < 0 || y >= mesh[0].length) {
            return false;
        }
        return mesh[x][y] == 0;
    }

    private void shiftPiece(form piece, int dCol, int dRow) {
        piece.a.setX(piece.a.getX() + dCol * SIZE);
        piece.a.setY(piece.a.getY() + dRow * SIZE);
        piece.b.setX(piece.b.getX() + dCol * SIZE);
        piece.b.setY(piece.b.getY() + dRow * SIZE);
        piece.c.setX(piece.c.getX() + dCol * SIZE);
        piece.c.setY(piece.c.getY() + dRow * SIZE);
        piece.d.setX(piece.d.getX() + dCol * SIZE);
        piece.d.setY(piece.d.getY() + dRow * SIZE);
    }

    private int gridX(javafx.scene.shape.Rectangle block) {
        return (int) block.getX() / SIZE;
    }

    private int gridY(javafx.scene.shape.Rectangle block) {
        return (int) block.getY() / SIZE;
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