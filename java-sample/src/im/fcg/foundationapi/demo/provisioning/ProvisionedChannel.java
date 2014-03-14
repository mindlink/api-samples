package im.fcg.foundationapi.demo.provisioning;

/**
 * Simple representation of a provisioned channel as returned by the API.
 */
public class ProvisionedChannel {

    private final String id;

    private ProvisionedChannelState state;

    /**
     * Creates a new instance of {@link ProvisionedChannel}.
     */
    protected ProvisionedChannel(final String id,
            final ProvisionedChannelState state) {
        this.id = id;
        this.state = state;
    }

    /**
     * Creates a new instance of {@link ProvisionedChannel} with the given ID.
     *
     * @param id The ID of the channel
     */
    public ProvisionedChannel(final String id) {
        this.id = id;
    }

    /**
     * Retrieves the ID of the channel.
     *
     * @return The channel's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the current state of the channel.
     *
     * @return The channel's state
     */
    public ProvisionedChannelState getState() {
        return state;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ProvisionedChannel{id=" + id + ", state=" + state + '}';
    }

}
