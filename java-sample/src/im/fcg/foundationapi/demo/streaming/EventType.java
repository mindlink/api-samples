package im.fcg.foundationapi.demo.streaming;

/**
 * Enumeration of possible event types that may be requested using the
 * streaming API.
 */
public enum EventType {

    /** Message events - for messages received in a set of given channels. */
    MESSAGE("message"),
    /** Meta-data events - for any changes to the agent's meta-data. */
    METADATA("meta-data"),
    /** Channel state events - for any changes to the state of a channel. */
    CHANNEL_STATE("channel-state");

    private final String requestForm;

    private EventType(final String requestForm) {
        this.requestForm = requestForm;
    }

    /**
     * Gets the 'Request form' of the event type - i.e., the string that should
     * be passed to the API in order to request this event type.
     *
     * @return This type's string representation
     */
    public String getRequestForm() {
        return requestForm;
    }

}
