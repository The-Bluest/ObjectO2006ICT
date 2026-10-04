package org.example;

public class PlayerMoveCommand implements GameCommand {
    private final PlayerBoard player;
    private final PlayerBoard.Action action;
    public PlayerMoveCommand(
            PlayerBoard player,
            PlayerBoard.Action action
    ) {
        this.player = player;
        this.action = action;
    }
    @Override
    public void execute() {
        player.handleInput(action);
    }
}