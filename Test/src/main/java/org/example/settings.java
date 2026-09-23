package org.example;

/**
 * Backwards-compatible alias for older code that still refers to
 * {@code settings}. New code should use {@link Settings}.
 */
@Deprecated
public class settings extends Settings {
    public settings() {
        super();
    }
}