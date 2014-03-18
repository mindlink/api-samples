package com.mindlinksoft.foundationapi.demo;

/**
 * Enumeration of known presence states.
 */
public enum PresenceState {

    /** Presence not known or not applicable to channel. */
    UNKNOWN(0),
    /** Channel is available. */
    AVAILABLE(100),
    /** Channel is available but has been idle a period of time. */
    AVAILABLE_IDLE(150),
    /** Channel is busy. */
    BUSY(200),
    /** Channel is busy but has been idle a period of time. */
    BUSY_IDLE(250),
    /** Channel is marked as "do not disturb". */
    DO_NOT_DISTURB(300),
    /** Channel is marked as "be right back". */
    BE_RIGHT_BACK(400),
    /** Channel is marked as "away". */
    AWAY(500),
    /** Channel is offline. */
    OFFLINE(600);

    private final int value;

    /**
     * Creates a new instance of {@link PresenceState}.
     */
    private PresenceState(final int value) {
        this.value = value;
    }

    /**
     * Gets the numeric value associated with this presence state.
     *
     * @return This state's numeric value
     */
    public int getValue() {
        return value;
    }

    /**
     * Finds a {@link PresenceState} entry for the given numeric value.
     *
     * @param value The value to look up
     * @return A corresponding {@link PresenceState} entry, or {@link #UNKNOWN}
     * if no state can be found with the given value.
     */
    public static PresenceState forValue(final int value) {
        for (PresenceState state : values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        return PresenceState.UNKNOWN;
    }

}
