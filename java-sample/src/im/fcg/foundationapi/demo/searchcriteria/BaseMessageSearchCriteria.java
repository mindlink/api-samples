package im.fcg.foundationapi.demo.searchcriteria;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains common criteria for all searches.
 */
public class BaseMessageSearchCriteria implements MessageSearchCriteria {

    private final String searchTerm;
    private final boolean matchCase;
    private final boolean matchExact;
    private final boolean matchAll;
    private final int limit;
    private final String[] channelIds;

    /**
     * Creates a new instance of {@link BaseMessageSearchCriteria}.
     *
     * @param searchTerm The term to search for (may be empty)
     * @param matchCase Whether or not to match the case of the search term
     * @param matchExact Whether or not to match the search term exactly
     * (i.e., as a phrase rather than as individual words)
     * @param matchAll Whether or not to match all parts of the search term
     * (instead of matching any single word)
     * @param limit The maximum number of messages to return
     * @param channelIds The set of channels to search within
     */
    public BaseMessageSearchCriteria(final String searchTerm,
            final boolean matchCase, final boolean matchExact,
            final boolean matchAll, final int limit,
            final String[] channelIds) {
        this.searchTerm = searchTerm;
        this.matchCase = matchCase;
        this.matchExact = matchExact;
        this.matchAll = matchAll;
        this.limit = limit;
        this.channelIds = Arrays.copyOf(channelIds, channelIds.length);
    }

    /**
     * Retrieves the set of channel that will be searched.
     *
     * @return The channels to be searched
     */
    public String[] getChannelIds() {
        return Arrays.copyOf(channelIds, channelIds.length);
    }

    /**
     * Gets the maximum number of messages to return
     *
     * @return The maximum number of messages to return
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Determines whether all the search terms should be matched.
     *
     * @return <code>true</code> if all words must match; <code>false</code> if
     * only some of the terms must be matched
     */
    public boolean shouldMatchAll() {
        return matchAll;
    }

    /**
     * Determines whether the search terms should be matched case-sensitively.
     *
     * @return <code>true</code> for case-sensitive matches, <code>false</code>
     * otherwise
     */
    public boolean shouldMatchCase() {
        return matchCase;
    }

    /**
     * Determines whether the search terms should be matched exactly
     * (as a phrase, instead of as individual words).
     *
     * @return <code>true</code> for an exact match, <code>false</code>
     * otherwise
     */
    public boolean shouldMatchExact() {
        return matchExact;
    }

    /**
     * Gets the search terms.
     *
     * @return The terms to search for
     */
    public String getSearchTerm() {
        return searchTerm;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "BaseMessageSearchCriteria{searchTerm=" + searchTerm
                + ", matchCase=" + matchCase + ", matchExact=" + matchExact
                + ", matchAll=" + matchAll + ", limit=" + limit
                + ", channelIds=" + channelIds + '}';
    }

    /** {@inheritDoc} */
    @Override
    public void putData(final JSONObject object) throws JSONException {
        object.put("SearchTerm", searchTerm);
        object.put("MatchCase", matchCase);
        object.put("MatchExact", matchExact);
        object.put("MatchExact", matchAll);
        object.put("Limit", limit);
        object.put("ChannelIds", new JSONArray(Arrays.asList(channelIds)));
    }

}
