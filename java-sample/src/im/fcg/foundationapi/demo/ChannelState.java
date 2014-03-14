package im.fcg.foundationapi.demo;

/**
 * Simple representation of a channel's state as returned by the API.
 */
public class ChannelState {

    private final String subject;
    private final PresenceState presence;
    private final String presenceText;

    /**
     * Creates a new instance of {@link ChannelState}.
     */
    protected ChannelState(final String subject, final PresenceState presence,
            final String presenceText) {
        this.subject = subject;
        this.presence = presence;
        this.presenceText = presenceText;
    }

    /**
     * Gets the current presence state of the channel.
     *
     * @return The channel's current presence
     */
    public PresenceState getPresence() {
        return presence;
    }

    /**
     * Gets the current presence text of the channel.
     *
     * @return The channel's current presence text (or <code>null</code> if
     * no text is set)
     */
    public String getPresenceText() {
        return presenceText;
    }

    /**
     * Gets the current subject of the channel.
     *
     * @return The channel's current subject (or <code>null</code> if none is
     * set)
     */
    public String getSubject() {
        return subject;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ChannelState{subject=" + subject + ", presence=" + presence
                + ", presenceText=" + presenceText + '}';
    }

}
