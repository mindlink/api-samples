package com.mindlinksoft.foundationapi.demo.streaming;

/**
 * Specialised {@link Event} for describing received messages.
 */
public class MessageEvent extends Event {

    private final String channelId;
    private final String senderId;
    private final String subject;
    private final String content;
    private final String messageParts;

    /**
     * Creates a new instance of {@link MessageEvent}.
     */
    public MessageEvent(final long eventId, final long time,
            final String channelId, final String senderId, final String subject,
            final String content, final String messageParts) {
        super(eventId, time);

        this.channelId = channelId;
        this.senderId = senderId;
        this.subject = subject;
        this.content = content;
        this.messageParts = messageParts;
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
     * @return The message sender's ID
     */
    public String getSenderId() {
        return senderId;
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

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "MessageEvent{super=" + super.toString() + ", channelId="
                + channelId + ", senderId=" + senderId + ", subject=" + subject
                + ", content=" + content + ", messageParts=" + messageParts + '}';
    }

}
