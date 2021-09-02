package com.mindlinksoft.foundationapi.demo;

/**
 * Simple representation of a message as returned by the API.
 */
public class Message {

    private final String id;
    private final boolean isAlert;
    private final String senderId;
    private final String senderAlias;
    private final String channelId;
    private final String subject;
    private final String text;
    private final String messageParts;
    private final long timestamp;
    private final String token;
    private final String classification;
    private final String securityContext;
    private final String dataAttributes;

    /**
     * Creates a new instance of {@link Message}.
     */
    protected Message(final String id, final boolean isAlert,
            final String senderId, final String senderAlias, final String channelId, final String subject,
            final String text, final String messageParts, final long timestamp, final String token,
            final String classification, final String securityContext, final String dataAttributes) {
        this.id = id;
        this.isAlert = isAlert;
        this.senderId = senderId;
        this.senderAlias = senderAlias;
        this.channelId = channelId;
        this.subject = subject;
        this.text = text;
        this.messageParts = messageParts;
        this.timestamp = timestamp;
        this.token = token;
        this.classification = classification;
        this.securityContext = securityContext;
        this.dataAttributes = dataAttributes;
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
     * Gets the alias of the sender of the message.
     *
     * @return The message sender's alias.
     */
    public String getSenderAlias() {
        return senderAlias;
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
     * Gets the message content as a collection of JSON message parts.
     *
     * @return The message parts JSON string.
     */
    public String getMessageParts() {
    	return this.messageParts;
    }

    /**
     * Gets the unix timestamp at which the story was sent.
     *
     * @return The message's timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    public String getToken() {
        return token;
    }

    /**
     * Gets the message classification.
     * @return The message classification.
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Gets the message securityContext.
     * @return The message security context.
     */
    public String getSecurityContext() {
        return securityContext;
    }

    /**
     * Gets the message data attributes.
     * @return The message data attributes.
     */
    public String getDataAttributes() {
        return dataAttributes;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Message{id=" + id + ", isAlert=" + isAlert + ", senderId="
                + senderId + ", senderAlias=" + senderAlias + ", channelId=" + channelId + ", subject="
                + subject + ", text=" + text + ", messageParts=" + messageParts
                + ", timestamp=" + timestamp
                + ", token=" + token
                + ", classification=" + classification
                + ", securityContext=" + securityContext
                + ", dataAttributes=" + dataAttributes + "}";
    }

}
