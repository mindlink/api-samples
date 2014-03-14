package im.fcg.foundationapi.demo.searchcriteria;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Search criteria that goes back a given number of days.
 */
public class DaysBackMessageSearchCriteria extends BaseMessageSearchCriteria {

    private final int daysBack;

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
     * @param daysBack The number of days back to search
     */
    public DaysBackMessageSearchCriteria(final String searchTerm,
            final boolean matchCase, final boolean matchExact,
            final boolean matchAll, final int limit, final String[] channelIds,
            final int daysBack) {
        super(searchTerm, matchCase, matchExact, matchAll, limit, channelIds);
        this.daysBack = daysBack;
    }

    /**
     * Gets the number of days history that should be searched.
     *
     * @return The number of days to search
     */
    public int getDaysBack() {
        return daysBack;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "DaysBackMessageSearchCriteria{super=" + super.toString()
                + ", daysBack=" + daysBack + '}';
    }

    /** {@inheritDoc} */
    @Override
    public void putData(final JSONObject object) throws JSONException {
        super.putData(object);

        object.put("DaysBack", daysBack);
    }
}
