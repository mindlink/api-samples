package im.fcg.foundationapi.demo.streaming;

/**
 * Contains properties common to all events returned by the streaming API.
 */
public abstract class Event {

    private final long eventId;
    private final long time;

    /**
     * Creates a new instance of {@link Event}.
     */
    protected Event(final long eventId, final long time) {
        this.eventId = eventId;
        this.time = time;
    }

    /**
     * Gets the ID of this event.
     *
     * @return This event's ID
     */
    public long getEventId() {
        return eventId;
    }

    /**
     * Gets the unix timestamp at which this even occurred.
     *
     * @return This event's timestamp
     */
    public long getTime() {
        return time;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Event{eventId=" + eventId + ", time=" + time + '}';
    }

}
