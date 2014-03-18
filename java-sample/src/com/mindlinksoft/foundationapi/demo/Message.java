package com.mindlinksoft.foundationapi.demo;

/**
 * Simple representation of a message as returned by the API.
 */
public class Message {

    private final String id;
    private final boolean isAlert;
    private final String senderId;
    private final String channelId;
    private final String subject;
    private final String text;
    private final long timestamp;

    /**
     * Creates a new instance of {@link Message}.
     */
    protected Message(final String id, final boolean isAlert,
            final String senderId, final String channelId, final String subject,
            final String text, final long timestamp) {
        this.id = id;
        this.isAlert = isAlert;
        this.senderId = senderId;
        this.channelId = channelId;
        this.subject = subject;
        this.text = text;
        this.timestamp = timestamp;
    }

    /**
     * Gets the ID of the channel which the message was sent to.
     *
     * @return The ID of the channel the message was sent to. May be
     * <code>null</code> if the ID has been specified elsewhere, for example
     * for messages within a {@link SearchResultSet}.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Gets the ID of the message.
     *
     * @return The message's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Determines if the message is an alert.
     *
     * @return <code>true</code> if the message is an alert; <code>false</code>
     * if it is not
     */
    public boolean isAlert() {
        return isAlert;
    }

    /**
     * Gets the ID of the sender of the message.
     *
     * @return The ID of the message's sender
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Gets the subject of the message (if it has been sent as a "story").
     *
     * @return The message's subject, or <code>null</code> for non-story
     * messages
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the textual content of the message.
     *
     * @return The message's text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the unix timestamp at which the story was sent.
     *
     * @return The message's timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Message{id=" + id + ", isAlert=" + isAlert + ", senderId="
                + senderId + ", channelId=" + channelId + ", subject="
                + subject + ", text=" + text + ", timestamp=" + timestamp + '}';
    }

}
