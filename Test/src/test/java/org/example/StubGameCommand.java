package org.example;

// Stub: returns a fixed, canned outcome (either does nothing, or always throws) regardless of context.
class StubGameCommand implements GameCommand {
    private final RuntimeException canned;

    StubGameCommand() {
        this(null);
    }

    StubGameCommand(RuntimeException canned) {
        this.canned = canned;
    }

    @Override
    public void execute() {
        if (canned != null) {
            throw canned;
        }
    }
}
