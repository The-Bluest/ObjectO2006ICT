// Advanced Pattern 2: State Pattern

package org.example;

public class PausedState implements GameState {

    @Override
    public void handleTick(TwoPlayerGame game, int tickMs) {
    }

    @Override
    public void handleCommand(GameCommand command) {
    }

    @Override
    public GameState toggleState() {
        return new RunningState();
    }

    @Override
    public boolean isPaused() {
        return true;
    }
}