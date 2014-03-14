package im.fcg.foundationapi.demo.provisioning;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import im.fcg.foundationapi.demo.AuthenticatingAgent;

/**
 * An agent capable of interacting with the provisioning services of the
 * foundation API.
 */
public class ProvisioningAgent extends AuthenticatingAgent {

    /**
     * Creates a new {@link ProvisioningAgent}.
     *
     * @param baseUrl The base address for the Foundation API. The agent will
     * append method names automatically. For example, a base URL of
     * <code>http://api.company.com</code> will result in URLs constructed such
     * as <code>http://api.company.com/Authentication/v1/Tokens</code>.
     * @param username The username to give to the API when authenticating
     * @param password The username to give to the API when authenticating
     * @param agent The ID of the agent to use. May be an empty string if
     * authenticating as a super user.
     */
    public ProvisioningAgent(final String baseUrl, final String username,
            final String password, final String agent) {
        super(baseUrl, username, password, agent);
    }

    /**
     * Gets the set of all provisioned agents.
     *
     * @return An array containing one {@link ProvisionedAgent} corresponding
     * to each agent provisioned on the API.
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public Collection<ProvisionedAgent> getAgents() throws IOException {
        try {
            final JSONArray response = new JSONArray(
                    getResponse("/Provisioning/v1/Agents", "GET", null));

            final ProvisionedAgent[] res
                    = new ProvisionedAgent[response.length()];
            for (int i = 0; i < response.length(); i++) {
                res[i] = getAgent(response.optJSONObject(i));
            }

            return Arrays.asList(res);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Gets an agent's information given its ID.
     *
     * @see #getAgents()
     * @param agentId The ID of the agent to retrieve
     * @return A single {@link ProvisionedAgent} instance corresponding to the
     * agent with the given ID
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public ProvisionedAgent getAgent(final String agentId) throws IOException {
        try {
            final JSONObject response = new JSONObject(
                    getResponse("/Provisioning/v1/Agents/" + agentId, "GET",
                    null));
            return getAgent(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Creates a new agent, or updates any existing agent with the same ID.
     *
     * @param agent A {@link ProvisionedAgent} instance containing details
     * about the agent
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void addOrUpdateAgent(final ProvisionedAgent agent)
            throws IOException {
        try {
            final JSONObject object = new JSONObject();

            object.put("Id", agent.getId());
            object.put("UserName", agent.getUsername());
            object.put("Channels", getChannels(agent.getChannels()));
            object.put("MetaData", getDictionary(agent.getMetadata()));
            object.put("CanProvision", String.valueOf(agent.canProvision()));
            object.put("Users", new JSONArray(agent.getUsers()));
            object.put("State", "0");

            getResponse("/Provisioning/v1/Agents/" + agent.getId(), "PUT",
                    object.toString());
        } catch (JSONException ex) {
            throw new IOException("Unable to construct JSON payload", ex);
        }
    }

    /**
     * Deletes an agent with the given ID.
     *
     * @param agentId The ID of the agent to be deleted
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void deleteAgent(final String agentId) throws IOException {
        getResponse("/Provisioning/v1/Agents/" + agentId, "DELETE", null);
    }

    /**
     * Retrieves the set of provisioned channels for a given agent.
     *
     * @param agentId The ID of the agent to retrieve channels for
     * @return The set of channels the given agent is provisioned in
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public ProvisionedChannel[] getChannels(final String agentId)
            throws IOException {
        try {
            final JSONArray response = new JSONArray(
                    getResponse("/Provisioning/v1/Agents/" + agentId
                    + "/Channels", "GET", null));
            return getProvisionedChannels(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Adds a new channel to the given agent's list of provisioned channels.
     *
     * @param agentId The ID of the agent to provision for a new channel
     * @param channel The channel to be added
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void addChannel(final String agentId,
            final ProvisionedChannel channel) throws IOException {
        try {
            getResponse("/Provisioning/v1/Agents/" + agentId + "/Channels/"
                    + channel.getId(), "PUT", getChannel(channel).toString());
        } catch (JSONException ex) {
            throw new IOException("Unable to construct JSON payload", ex);
        }
    }

    /**
     * Removes a provisioned channel from the specified agent.
     *
     * @param agentId The ID of the agent to remove the channel from
     * @param channelId The ID of the previously provisioned channel to be
     * removed
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void deleteChannel(final String agentId, final String channelId)
            throws IOException {
        getResponse("/Provisioning/v1/Agents/" + agentId + "/Channels/"
                + channelId, "DELETE", null);
    }

    /**
     * Gets the meta-data for a given agent. To retrieve a single entry from
     * the meta-data, use the {@link #getMetaData(String, String)} method, as
     * this only transfers the single requested value from the API.
     *
     * @param agentId The ID of the agent to retrieve meta-data for
     * @return The previously configured dictionary of meta-data for the agent
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public Map<String, String> getMetaData(final String agentId)
            throws IOException {
        try {
            final JSONArray response = new JSONArray(
                    getResponse("/Provisioning/v1/Agents/" + agentId
                    + "/MetaData", "GET", null));
            return getMap(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Completely replaces the given agent's meta-data with the given map.
     *
     * @param agentId The ID of the agent to replace meta-data for
     * @param metaData The new dictionary of meta-data for the agent
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void replaceMetaData(final String agentId,
            final Map<String, String> metaData) throws IOException {
        try {
            getResponse("/Provisioning/v1/Agents/" + agentId + "/MetaData",
                    "PUT", getDictionary(metaData).toString());
        } catch (JSONException ex) {
            throw new IOException("Unable to construct JSON payload", ex);
        }
    }

    /**
     * Retrieves the value of a single meta-data key for the given agent. To
     * retrieve multiple values, consider using {@link #getMetaData(String)}
     * as repeated requests to this method may incur a high HTTP overhead when
     * talking to the API in comparison.
     *
     * @param agentId The ID of the agent to retrieve meta-data for
     * @param key The meta-data key to retrieve the value of
     * @return The current value of the given meta-data key
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public String getMetaData(final String agentId, final String key)
            throws IOException {
        final JSONTokener tokener = new JSONTokener(
                getResponse("/Provisioning/v1/Agents/" + agentId + "/MetaData/"
                + key, "GET", null));

        try {
            return tokener.nextString(tokener.next());
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Deletes the entry with the specified key from the given agent's
     * meta-data. To delete all meta-data, call
     * {@link #replaceMetaData(String, Map)} with an empty map.
     *
     * @param agentId The ID of the agent to delete meta-data from
     * @param key The meta-data key to delete
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void deleteMetaData(final String agentId, final String key)
            throws IOException {
        getResponse("/Provisioning/v1/Agents/" + agentId + "/MetaData/"
                + key, "DELETE", null);
    }

    /**
     * Sets a single meta-data key/value pair for the given agent.
     *
     * @param agentId The ID of the agent to update meta-data for
     * @param key The meta-data key to be added or replaced
     * @param value The new value of the meta-data key
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void setMetaData(final String agentId, final String key,
            final String value) throws IOException {
        getResponse("/Provisioning/v1/Agents/" + agentId + "/MetaData/"
                + key, "PUT", JSONObject.quote(value));
    }

    /**
     * Attempts to find channels containing the given search term. Note that
     * this method requires an appropriately permissioned agent, and will not
     * function if authenticated as a superuser without an agent. The results
     * shown will be limited by the agent's permissions on the back-end chat
     * system; for full results, the agent account should be an administrator of
     * the system.
     *
     * @param term The term to search for
     * @returns A map of IDs to display names for each matching channel
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public Map<String, String> findChannels(final String term)
            throws IOException {
        try {
            final JSONArray response = new JSONArray(getResponse(
                    "/Provisioning/v1/Channels?query=" + term, "GET", null));
            return getMap(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Gets the set of all provisioned users.
     *
     * @see #getUser(java.lang.String)
     * @return An array of {@link ProvisionedUser}s representing users
     * provisioned to use the API
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public ProvisionedUser[] getUsers() throws IOException {
        try {
            final JSONArray response = new JSONArray(
                    getResponse("/Provisioning/v1/Users", "GET", null));

            final ProvisionedUser[] users
                    = new ProvisionedUser[response.length()];
            for (int i = 0; i < response.length(); i++) {
                users[i] = getUser(response.getJSONObject(i));
            }

            return users;
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Gets a single provisioned user.
     *
     * @param userId The ID of the user to look up
     * @return A single {@link ProvisionedUser} instance representing the
     * specified user.
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public ProvisionedUser getUser(final String userId) throws IOException {
        try {
            final JSONObject response = new JSONObject(
                    getResponse("/Provisioning/v1/Users/" + userId, "GET",
                    null));
            return getUser(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Creates a new user, or updates any existing user with the same ID.
     *
     * @param user The user to be added or updated
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void addOrUpdateUser(final ProvisionedUser user) throws IOException {
        try {
            getResponse("/Provisioning/v1/Users/" + user.getId(), "PUT",
                    getUser(user).toString());
        } catch (JSONException ex) {
            throw new IOException("Unable to construct JSON payload", ex);
        }
    }

    /**
     * Deletes a user with the given user ID.
     *
     * @param userId The ID of the user to delete
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void deleteUser(final String userId) throws IOException {
        getResponse("/Provisioning/v1/Users/" + userId, "DELETE", null);
    }

    /**
     * Utility method to serialise a {@link ProvisionedUser} into a JSON object.
     *
     * @param user The user to be serialised
     * @return A JSON object representing the user
     * @throws JSONException If the object cannot be created
     */
    protected JSONObject getUser(final ProvisionedUser user)
            throws JSONException {
        final JSONObject object = new JSONObject();
        object.put("UserId", user.getId());
        object.put("Username", user.getUsername());
        return object;
    }

    /**
     * Utility method to construct a {@link ProvisionedUser} instance from the
     * given JSON object.
     *
     * @param object The object to be converted
     * @return A corresponding {@link ProvisionedUser} instance
     * @throws JSONException If the object is missing required arguments
     */
    protected ProvisionedUser getUser(final JSONObject object)
            throws JSONException {
        return new ProvisionedUser(
                object.getString("UserId"),
                object.getString("Username"));
    }

    /**
     * Utility method to convert a list of {@link ProvisionedChannel}s into
     * a JSON array.
     *
     * @see #getChannel(ProvisionedChannel)
     * @param channels The channels to be converted
     * @return A JSON array containing objects corresponding to each channel
     * @throws JSONException If the array cannot be constructed
     */
    protected JSONArray getChannels(
            final Collection<ProvisionedChannel> channels)
            throws JSONException {
        final JSONArray array = new JSONArray();

        for (ProvisionedChannel channel : channels) {
            array.put(getChannel(channel));
        }

        return array;
    }

    /**
     * Utility method to convert a single {@link ProvisionedChannel} into
     * a JSON object which can be sent over the wire.
     *
     * @param channel The channel to be converted
     * @return A JSONObject representing the given channel
     * @throws JSONException If the channel cannot be converted
     */
    protected JSONObject getChannel(final ProvisionedChannel channel)
            throws JSONException {
        final JSONObject object = new JSONObject();
        object.put("Id", channel.getId());
        object.put("State", "0");
        return object;
    }

    /**
     * Utility method to convert a map into a WCF-style dictionary, consisting
     * of an array of key/value pairs.
     *
     * @param data The map to be converted
     * @return A JSON array containing one entry for each map entry consisting
     * of an object with appropriate 'Key' and 'Value' properties.
     * @throws JSONException If the map cannot be converted
     */
    protected JSONArray getDictionary(final Map<String, String> data)
            throws JSONException {
        final JSONArray array = new JSONArray();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            final JSONObject object = new JSONObject();
            object.put("Key", entry.getKey());
            object.put("Value", entry.getValue());
            array.put(object);
        }

        return array;
    }

    /**
     * Utility method to create a {@link ProvisionedAgent} from a JSON object
     * received from the API.
     *
     * @param object The object that was received
     * @return A corresponding {@link ProvisionedAgent}
     * @throws JSONException If the object is missing required arguments
     */
    protected ProvisionedAgent getAgent(final JSONObject object)
            throws JSONException {
        return new ProvisionedAgent(
                object.getString("Id"),
                object.getString("UserName"),
                Arrays.asList(getProvisionedChannels(
                object.optJSONArray("Channels"))),
                getMap(object.optJSONArray("MetaData")),
                object.optBoolean("CanProvision"),
                ProvisionedAgentState.values()[object.optInt("State")],
                Arrays.asList(getStrings(object.optJSONArray("Users"))));
    }

    /**
     * Utility method to convert a JSON array of strings into a proper
     * String array.
     *
     * @param array The JSON array to be converted
     * @return An array of strings corresponding to the given JSON array
     * @throws JSONException If the array cannot be converted
     */
    protected String[] getStrings(final JSONArray array) throws JSONException {
        if (array == null) {
            return new String[0];
        }

        final String[] res = new String[array.length()];

        for (int i = 0; i < array.length(); i++) {
            res[i] = array.getString(i);
        }

        return res;
    }

    /**
     * Utility method to convert a JSON array of channel objects into an array
     * of {@link ProvisionedChannel}s.
     *
     * @param array The array of channels to be converted
     * @return A corresponding array of {@link ProvisionedChannel}s
     * @throws JSONException If the channels cannot be converted
     * @see #getProvisionedChannel(org.json.JSONObject)
     */
    protected ProvisionedChannel[] getProvisionedChannels(final JSONArray array)
            throws JSONException {
        if (array == null) {
            return new ProvisionedChannel[0];
        }

        final ProvisionedChannel[] res = new ProvisionedChannel[array.length()];

        for (int i = 0; i < array.length(); i++) {
            res[i] = getProvisionedChannel(array.getJSONObject(i));
        }

        return res;
    }

    /**
     * Utility method to convert a single provisioned channel represented as
     * a JSON object into an actual {@link ProvisionedChannel}.
     *
     * @param object The object to be converted
     * @return A corresponding {@link ProvisionedChannel} instance
     * @throws JSONException If the channel cannot be converted
     */
    protected ProvisionedChannel getProvisionedChannel(final JSONObject object)
            throws JSONException {
        return new ProvisionedChannel(
                object.getString("Id"),
                ProvisionedChannelState.values()[object.optInt("State")]);
    }

}
