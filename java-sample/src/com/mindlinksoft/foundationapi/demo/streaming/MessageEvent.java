package com.mindlinksoft.foundationapi.demo.streaming;

/**
 * Specialised {@link Event} for describing received messages.
 */
public class MessageEvent extends Event {

    private final String channelId;
    private final String senderId;
    private final String senderAlias;
    private final String subject;
    private final String content;
    private final String messageParts;
    private final String token;
    private final String classification;
    private final String securityContext;
    private final String dataAttributes;

    /**
     * Creates a new instance of {@link MessageEvent}.
     */
    public MessageEvent(final long eventId, final long time,
            final String channelId, final String senderId, final String senderAlias, final String subject,
            final String content, final String messageParts, final String token,
            final String classification, final String securityContext, final String dataAttributes) {
        super(eventId, time);

        this.channelId = channelId;
        this.senderId = senderId;
        this.senderAlias = senderAlias;
        this.subject = subject;
        this.content = content;
        this.messageParts = messageParts;
        this.token = token;
        this.classification = classification;
        this.securityContext = securityContext;
        this.dataAttributes = dataAttributes;
    }

    /**
     * Gets the ID of the channel which the message was received on.
     *
     * @return The ID of the channel the message was received on
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Gets the textual content of the message.
     *
     * @return The message's content
     */
    public String getContent() {
        return content;
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
     * Gets the ID of the sender of the message.
     *
     * @return The message sender's ID.
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
     * Gets the subject of the message, if it was sent as a 'story'.
     *
     * @return The message's subject, or <code>null</code> for non-story
     * messages.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the message token.
     *
     * @return The message token, or {@code null} if there is no token.
     */
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
        return "MessageEvent{super=" + super.toString() + ", channelId="
                + channelId + ", senderId=" + senderId + ", senderAlias=" + senderAlias + ", subject=" + subject
                + ", content=" + content + ", messageParts=" + messageParts + ", token=" + token
                + ", classification=" + classification
                + ", securityContext=" + securityContext
                + ", dataAttributes=" + dataAttributes + "}";
    }

}
