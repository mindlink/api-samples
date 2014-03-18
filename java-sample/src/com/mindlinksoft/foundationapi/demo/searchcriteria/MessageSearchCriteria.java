package com.mindlinksoft.foundationapi.demo.searchcriteria;

import org.json.JSONException;
import org.json.JSONObject;

import com.mindlinksoft.foundationapi.demo.SimpleCollaborationAgent;

/**
 * Describes search criteria that may be used when searching for channel
 * messages.
 *
 * @see SimpleCollaborationAgent#searchChannels(MessageSearchCriteria)
 */
public interface MessageSearchCriteria {

    /**
     * Inserts the search criteria into the given JSON object.
     *
     * @param object The object to add search criteria to
     * @throws JSONException If arguments fail to serialise
     */
    void putData(final JSONObject object) throws JSONException;

}
