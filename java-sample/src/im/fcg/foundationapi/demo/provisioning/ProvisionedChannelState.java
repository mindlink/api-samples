package im.fcg.foundationapi.demo.provisioning;

/**
 * Enumeration of the possible states of a provisioned channel.
 */
public enum ProvisionedChannelState {

    /** The channel is inactive and may not be interacted with. */
    IDLE,

    /** The channel is activating and may become ready soon. */
    ACTIVATING,

    /** The channel is active and may be interacted with. */
    ACTIVE;

}
