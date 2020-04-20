package com.mindlinksoft.foundationapi.demo.management;

/**
 * Represents the privacy of a channel.
 */
public enum ChannelPrivacy {
    /**
     * Represents that the channel can be seen and joined by everybody.
     */
    Open,

    /**
     * Represents that the channel can be seen by everybody, but joined by members only.
     */
    Closed,

    /**
     * Represents that the channel can be seen and joined by members only.
     */
    Secret
}