package com.mindlinksoft.foundationapi.demo;

import java.util.Collections;
import java.util.Map;

/**
 * Simple representation of a channel's information as returned by the API.
 */
public class Channel {

    private final boolean canAcceptFiles;
    private final String description;
    private final String displayName;
    private final String emailAddress;
    private final String id;
    private final boolean isReadOnly;
    private final Map<String, String> metadata;
    private final String subject;

    /**
     * Creates a new instance of {@link Channel}.
     */
    protected Channel(final boolean canAcceptFiles, final String description,
            final String displayName, final String emailAddress,
            final String id, final boolean isReadOnly,
            final Map<String, String> metadata, final String subject) {
        this.canAcceptFiles = canAcceptFiles;
        this.description = description;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.id = id;
        this.isReadOnly = isReadOnly;
        this.metadata = metadata;
        this.subject = subject;
    }

    /**
     * Determines whether the channel can accept files.
     *
     * @return <code>true</code> if the channel accepts files;
     * <code>false</code> otherwise.
     */
    public boolean isCanAcceptFiles() {
        return canAcceptFiles;
    }

    /**
     * Retrieves the description for this channel.
     *
     * @return The channel's description (or <code>null</code> if none is set)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the display name for this channel.
     *
     * @return The channel's display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retrieves the email address associated with this channel.
     *
     * @return The channel's email address (or <code>null</code> if none is set)
     */
    public String getEmailAddress() {
        return emailAddress;
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
     * Determines whether the channel is read-only or not.
     *
     * @return <code>true</code> if the channel is read only (i.e., cannot
     * accept messages from the agent); <code>false</code> otherwise
     */
    public boolean isIsReadOnly() {
        return isReadOnly;
    }

    /**
     * Retrieves the meta-data associated with this channel.
     *
     * @return This channel's meta-data
     */
    public Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    /**
     * Retrieves the subject for this channel.
     *
     * @return The channel's subject (or <code>null</code> if none is set)
     */
    public String getSubject() {
        return subject;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ChannelInformation{canAcceptFiles=" + canAcceptFiles
                + ", description=" + description + ", displayName="
                + displayName + ", emailAddress=" + emailAddress + ", id="
                + id + ", isReadOnly=" + isReadOnly + ", metadata=" + metadata
                + ", subject=" + subject + '}';
    }
}
