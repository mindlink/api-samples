package com.mindlinksoft.foundationapi.demo.searchcriteria;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Search criteria that specifies a range of dates.
 */
public class DateRangeMessageSearchCriteria extends BaseMessageSearchCriteria {

    private final String fromDate;
    private final String toDate;

    /**
     * Creates a new instance of {@link DateRangeMessageSearchCriteria}.
     *
     * @param searchTerm The term to search for (may be empty)
     * @param matchCase Whether or not to match the case of the search term
     * @param matchExact Whether or not to match the search term exactly
     * (i.e., as a phrase rather than as individual words)
     * @param matchAll Whether or not to match all parts of the search term
     * (instead of matching any single word)
     * @param limit The maximum number of messages to return
     * @param channelIds The set of channels to search within
     * @param fromDate The date to start searching from
     * @param toDate The date to search until
     */
    public DateRangeMessageSearchCriteria(final String searchTerm,
            final boolean matchCase, final boolean matchExact,
            final boolean matchAll, final int limit, final String[] channelIds,
            final String fromDate, final String toDate) {
        super(searchTerm, matchCase, matchExact, matchAll, limit, channelIds);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Gets the date to start searching from.
     *
     * @return The 'from' date
     */
    public String getFromDate() {
        return fromDate;
    }

    /**
     * Gets the date to stop searching at.
     *
     * @return The 'to' date
     */
    public String getToDate() {
        return toDate;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "DateRangeMessageSearchCriteria{super=" + super.toString()
                + ", fromDate=" + fromDate + ", toDate=" + toDate + '}';
    }

    /** {@inheritDoc} */
    @Override
    public void putData(final JSONObject object) throws JSONException {
        super.putData(object);

        object.put("FromDate", fromDate);
        object.put("ToDate", toDate);
    }

}
