package com.mindlinksoft.foundationapi.demo.provisioning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple representation of a provisioned agent as returned by the API. Note
 * that while this implementation permits modifying its properties, it must
 * be passed to the {@link ProvisioningAgent#addOrUpdateAgent(ProvisionedAgent)}
 * method for the changes to be applied in the API.
 */
public class ProvisionedAgent {

    private final Collection<ProvisionedChannel> channels
            = new ArrayList<ProvisionedChannel>();

    private final Map<String, String> metadata
            = new HashMap<String, String>();

    private final Collection<String> users
            = new ArrayList<String>();

    private final String id;

    private String username;

    private boolean canProvision;

    private ProvisionedAgentState state;

    /**
     * Creates a new provisioned agent with no channels, metadata or users.
     *
     * @param id The ID of the agent
     * @param username The username the agent should use to connect to the
     * back-end chat system
     */
    public ProvisionedAgent(final String id, final String username) {
        this.id = id;
        this.username = username;
    }

    /**
     * Creates a new instance of {@link ProvisionedAgent}.
     */
    protected ProvisionedAgent(final String id, final String username,
            final Collection<ProvisionedChannel> channels,
            final Map<String, String> metadata, final boolean canProvision,
            final ProvisionedAgentState state, final Collection<String> users) {
        this(id, username, channels, metadata, canProvision, users);

        this.state = state;
    }

    /**
     * Creates a new provisioned agent.
     *
     * @param id The ID of the agent
     * @param username The username the agent should use to connect to the
     * back-end chat system
     * @param channels The set of channels the agent is permitted to access
     * @param metadata The meta-data for the agent
     * @param canProvision Whether the agent is allowed to access the
     * provisioning API
     * @param users The set of user IDs that may access the agent
     */
    public ProvisionedAgent(final String id, final String username,
            final Collection<ProvisionedChannel> channels,
            final Map<String, String> metadata, final boolean canProvision,
            final Collection<String> users) {
        this(id, username);

        this.channels.addAll(channels);
        this.metadata.putAll(metadata);
        this.canProvision = canProvision;
        this.users.addAll(users);
    }

    /**
     * Gets a <strong>modifiable</strong> collection of channels that the
     * agent is provisioned to use.
     *
     * @return The agent's collection of provisioned channels
     */
    public Collection<ProvisionedChannel> getChannels() {
        return channels;
    }

    /**
     * Gets the unique ID of the agent
     *
     * @return The agent's unique ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets a <strong>modifiable</strong> copy of the agent's meta-data.
     *
     * @return A map of the agent's meta-data keys to their values
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Gets a <strong>modifiable</strong> set of users who are allowed to
     * access the agent.
     *
     * @return The set of users allowed to access the agent
     */
    public Collection<String> getUsers() {
        return users;
    }

    /**
     * Determines whether this agent is allowed to access the provisioning API.
     *
     * @return <code>true</code> if the agent may access the provisioning API;
     * <code>false</code> otherwise
     */
    public boolean canProvision() {
        return canProvision;
    }

    /**
     * Sets whether this agent can access the provisioning API.
     *
     * @param canProvision <code>true</code> if the agent should be allowed
     * access; <code>false</code> otherwise
     */
    public void setCanProvision(final boolean canProvision) {
        this.canProvision = canProvision;
    }

    /**
     * Gets the current state of the agent.
     *
     * @return The agent's current state
     */
    public ProvisionedAgentState getState() {
        return state;
    }

    /**
     * Gets the username the agent uses to connect to the back-end chat system.
     *
     * @return The agent's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username that the agent should use to connect to the back-end
     * chat system.
     *
     * @param username The agent's new username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ProvisionedAgent{id=" + id + ", username=" + username
                + ", channels=" + channels + ", metadata=" + metadata
                + ", canProvision=" + canProvision + ", state=" + state
                + ", users=" + users + '}';
    }
}
