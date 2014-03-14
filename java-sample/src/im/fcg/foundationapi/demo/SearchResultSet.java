package im.fcg.foundationapi.demo;

import java.util.Collections;
import java.util.List;

/**
 * Describes a set of results from a search.
 */
public class SearchResultSet {

    private final String channelId;
    private final int count;
    private final String maxMessageId;
    private final List<Message> messages;
    private final String minMessageId;

    protected SearchResultSet(final String channelId, final int count,
            final String maxMessageId, final List<Message> messages,
            final String minMessageId) {
        this.channelId = channelId;
        this.count = count;
        this.maxMessageId = maxMessageId;
        this.messages = messages;
        this.minMessageId = minMessageId;
    }

    /**
     * Gets the ID of the channel the result set describes.
     *
     * @return The channel's ID
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Gets the number of messages in this result set.
     *
     * @return This set's message count
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the "maximum" message ID contained within the set.
     *
     * @return The max message ID in the set
     */
    public String getMaxMessageId() {
        return maxMessageId;
    }

    /**
     * Gets the messages in this result set.
     *
     * @return The list of {@link Message}s this result set contains
     */
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Gets the "minimum" message ID contained within the set.
     *
     * @return The min message ID in the set
     */
    public String getMinMessageId() {
        return minMessageId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "SearchResultSet{channelId=" + channelId + ", count=" + count
                + ", maxMessageId=" + maxMessageId + ", messages=" + messages
                + ", minMessageId=" + minMessageId + '}';
    }

}
