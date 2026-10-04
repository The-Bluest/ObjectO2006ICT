package org.example;

public class RunningState implements GameState {

    @Override
    public void handleTick (TwoPlayerGame game, int tickMs) {
        game.updatePlayers(tickMs);
    }

    @Override
    public void handleCommand(GameCommand command) {
        if (command != null) {
            command.execute();
        }
    }

    @Override
    public GameState toggleState() {
        return new PausedState();
    }

    @Override
    public boolean isPaused() {
        return false;
    }
}