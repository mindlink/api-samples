package com.mindlinksoft.foundationapi.demo.provisioning;

/**
 * Enumeration of the possible states of a provisioned agent.
 */
public enum ProvisionedAgentState {

    /** The agent is currently inactive and may not be used. */
    IDLE,

    /** The agent is currently attempting to activate. */
    ACTIVATING,

    /** The agent is active and may be used. */
    ACTIVE;

}
