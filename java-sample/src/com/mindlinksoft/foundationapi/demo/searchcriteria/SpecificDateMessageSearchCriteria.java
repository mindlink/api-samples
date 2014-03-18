package com.mindlinksoft.foundationapi.demo.searchcriteria;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Search criteria to search for messages on a specific date.
 */
public class SpecificDateMessageSearchCriteria extends BaseMessageSearchCriteria {

    private final String onDate;

    /**
     * Creates a new instance of {@link DaysBackMessageSearchCriteria}.
     *
     * @param searchTerm The term to search for (may be empty)
     * @param matchCase Whether or not to match the case of the search term
     * @param matchExact Whether or not to match the search term exactly
     * (i.e., as a phrase rather than as individual words)
     * @param matchAll Whether or not to match all parts of the search term
     * (instead of matching any single word)
     * @param limit The maximum number of messages to return
     * @param channelIds The set of channels to search within
     * @param onDate The date to search for messages from
     */
    public SpecificDateMessageSearchCriteria(final String searchTerm,
            final boolean matchCase, final boolean matchExact,
            final boolean matchAll, final int limit, final String[] channelIds,
            final String onDate) {
        super(searchTerm, matchCase, matchExact, matchAll, limit, channelIds);
        this.onDate = onDate;
    }

    /**
     * Gets the date to search for messages from.
     *
     * @return The single date to search for messages
     */
    public String getOnDate() {
        return onDate;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "SpecificDateMessageSearchCriteria{super=" + super.toString()
                + ", onDate=" + onDate + '}';
    }

    /** {@inheritDoc} */
    @Override
    public void putData(final JSONObject object) throws JSONException {
        super.putData(object);

        object.put("OnDate", onDate);
    }

}
