package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PausedStateTest {

    @Test
    void isPaused() {
        assertTrue(new PausedState().isPaused());
    }

    @Test
    void toggleStateSwitchesToRunning() {
        assertInstanceOf(RunningState.class, new PausedState().toggleState());
    }

    @Test
    void handleCommandDoesNotExecuteTheGivenCommand() {
        SpyGameCommand command = new SpyGameCommand();

        new PausedState().handleCommand(command);

        assertFalse(command.wasExecuted());
    }

    @Test
    void handleCommandNeverRunsAPoisonPillStub() {
        StubGameCommand poisonPill =
                new StubGameCommand(new RuntimeException("must never execute while paused"));

        assertDoesNotThrow(() -> new PausedState().handleCommand(poisonPill));
    }

    @Test
    void handleTickIsANoOp() {
        assertDoesNotThrow(() -> new PausedState().handleTick(null, 30));
    }
}
