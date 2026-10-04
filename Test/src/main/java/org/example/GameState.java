// Advanced Pattern 2: State Pattern

package org.example;

public interface GameState {
    void handleTick(TwoPlayerGame game, int tickMs);
    void handleCommand(GameCommand command);
    GameState toggleState();
    boolean isPaused();
}
