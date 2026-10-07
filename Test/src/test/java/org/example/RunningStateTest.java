package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RunningStateTest {

    @Test
    void isNotPaused() {
        assertFalse(new RunningState().isPaused());
    }

    @Test
    void toggleStateSwitchesToPaused() {
        assertInstanceOf(PausedState.class, new RunningState().toggleState());
    }

    @Test
    void handleCommandExecutesTheGivenCommand() {
        SpyGameCommand command = new SpyGameCommand();

        new RunningState().handleCommand(command);

        assertTrue(command.wasExecuted());
        assertEquals(1, command.executionCount());
    }

    @Test
    void handleCommandDoesNotCareWhatTheCommandDoesInternally() {
        assertDoesNotThrow(() -> new RunningState().handleCommand(new StubGameCommand()));
    }

    @Test
    void handleCommandIgnoresANullCommand() {
        assertDoesNotThrow(() -> new RunningState().handleCommand(null));
    }
}
