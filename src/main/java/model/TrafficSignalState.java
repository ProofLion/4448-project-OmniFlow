package model;

/**
 * Minimal signal states used by the city layouts.
 */
public enum TrafficSignalState {
    GREEN,
    YELLOW,
    RED;

    public boolean allowsMovement() {
        return this == GREEN;
    }
}
