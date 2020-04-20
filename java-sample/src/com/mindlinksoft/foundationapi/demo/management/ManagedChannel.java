package com.mindlinksoft.foundationapi.demo.management;

/**
 * Represents a channel in the management API.
 */
public final class ManagedChannel {
    private String id;
    private String name;
    private ChannelPrivacy privacy;

    /**
     * Creates a new instance of {@link ManagedChannel}.
     * 
     * @param id   The ID of the channel.
     * @param name The name of the channel.
     * @param privacy The privacy of the channel.
     */
    protected ManagedChannel(String id, String name, ChannelPrivacy privacy) {
        this.id = id;
        this.name = name;
        this.privacy = privacy;
    }

    /**
     * Gets the ID of the channel.
     * @return The ID of the channel.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the name of the channel.
     * @return The name of the channel.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the channel privacy.
     * @return The privacy of the channel.
     */
    public ChannelPrivacy getPrivacy() {
        return this.privacy;
    }
}