package org.example;

// Spy: a real GameCommand that records how many times it was executed, so a test can verify the interaction afterwards.
class SpyGameCommand implements GameCommand {
    private int executionCount = 0;

    @Override
    public void execute() {
        executionCount++;
    }

    boolean wasExecuted() {
        return executionCount > 0;
    }

    int executionCount() {
        return executionCount;
    }
}
