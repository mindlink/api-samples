package im.fcg.foundationapi.demo.streaming;

/**
 * Specialised {@link Event} for describing channel state changes.
 */
public class ChannelStateEvent extends Event {

    private final String channelId;
    private final boolean active;

    /**
     * Creates a new instance of {@link ChannelStateEvent}.
     */
    public ChannelStateEvent(final long eventId, final long time,
            final String channelId, final boolean active) {
        super(eventId, time);

        this.channelId = channelId;
        this.active = active;
    }

    /**
     * Determines whether the channel is now active.
     *
     * @return <code>true</code> if the event indicates the channel is active;
     * <code>false</code> if the channel has become inactive
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the ID of the channel which the event concerns.
     *
     * @return The channel's ID
     */
    public String getChannelId() {
        return channelId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ChannelStateEvent{super=" + super.toString() + ", channelId="
                + channelId + ", active=" + active + '}';
    }

}
