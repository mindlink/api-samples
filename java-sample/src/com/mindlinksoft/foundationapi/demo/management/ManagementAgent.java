package com.mindlinksoft.foundationapi.demo.management;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.mindlinksoft.foundationapi.demo.AuthenticatingAgent;

/**
 * An agent capable of interacting with the management services of the
 * foundation API.
 */
public class ManagementAgent extends AuthenticatingAgent {

    /**
     * Creates a new {@link ManagementAgent}.
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
    public ManagementAgent(final String baseUrl, final String username,
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
    public Collection<ManagedChannel> getManagedChannels() throws IOException {
        try {
            final JSONArray response = new JSONArray(
                    getResponse("/Management/v1/Channels", "GET", null));

            final ManagedChannel[] res
                    = new ManagedChannel[response.length()];
            for (int i = 0; i < response.length(); i++) {
                res[i] = getManagedChannel(response.optJSONObject(i));
            }

            return Arrays.asList(res);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Gets the {@link ManagedChannel} with the given id.
     * @param id The ID of the channel
     * @return The managed channel
     * @throws IOException If the request could not be completed or the response not parsed
     */
    public ManagedChannel getManagedChannel(final String id) throws IOException {
        try {
            final JSONObject response = new JSONObject(
                getResponse("/Management/v1/Channels/" + id, "GET", null));

            return getManagedChannel(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialize JSON response", ex);
        }
    }

    /**
     * Creates a new channel.
     *
     * @param name The name of the channel
     * @param categoryId The parent category (see {@link #getCategories()})
     * @param description The description of the channel.
     * @param privacy The privacy of the channel
     * @param members The collection of member resource IDs
     * @return The ID of the created channel.
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public String addManagedChannel(final String name, final String categoryId, final String description, final ChannelPrivacy privacy, final Collection<String> members) throws IOException {
        try {
            final JSONObject object = new JSONObject();

            object.put("Name", name);
            object.put("CategoryId", categoryId);
            object.put("Description", description);
            object.put("Privacy", privacy.ordinal());
            object.put("Members", new JSONArray(members));

            JSONTokener result = new JSONTokener(
                getResponse("/Management/v1/Channels", "POST", object.toString()));

            return (String)result.nextValue();
        } catch (JSONException ex) {
            throw new IOException("Unable to construct JSON payload", ex);
        }
    }

    /**
     * Deletes an existing managed channel.
     *
     * @param id The ID of the channel to delete.
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void deleteManagedChannel(final String id)
            throws IOException {
        getResponse("/Management/v1/Channels/" + id, "DELETE", null);
    }

    /**
     * Retrieves the set of available categories for a given agent.
     *
     * @return The set of categories available to the agent
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public Collection<ManagedCategory> getCategories()
            throws IOException {
        try {
            final JSONArray response = new JSONArray(
                    getResponse("/Management/v1/Categories", "GET", null));
            return getCategories(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Gets the members of a given channel.
     * @param channelId The ID of the channel
     * @return The collection of members in the given channel.
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public Collection<String> getChannelMembers(final String channelId) throws IOException {
        try {
            final JSONArray response = new JSONArray(
                    getResponse("/Management/v1/Channels/" + channelId + "/Members", "GET", null));
    
            final String[] res = new String[response.length()];
    
            for (int i = 0; i < response.length(); i++) {
                res[i] = response.getString(i);
            }
    
            return Arrays.asList(res);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Sets the members of a given channel.
     * @param channelId The ID of the channel.
     * @param members The collection to set as the channel members.
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public void setChannelMembers(final String channelId, final Collection<String> members) throws IOException {
        final JSONArray object = new JSONArray(members);

        getResponse("/Management/v1/Channels/" + channelId + "/Members", "PUT", object.toString());
    }

    /**
     * Utility method to create a {@link ManagedChannel} from a JSON object
     * received from the API.
     *
     * @param object The object that was received
     * @return A corresponding {@link ManagedChannel}
     * @throws JSONException If the object is missing required arguments
     */
    protected ManagedChannel getManagedChannel(final JSONObject object)
            throws JSONException {
        return new ManagedChannel(
                object.getString("Id"),
                object.getString("Name"),
                ChannelPrivacy.values()[object.optInt("Privacy")]);
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
     * Utility method to convert a JSON array of category objects into an array
     * of {@link ManagedCategory} instances.
     *
     * @param array The array of categories to be converted
     * @return A corresponding array of {@link ManagedCategory} instances
     * @throws JSONException If the categories cannot be converted
     */
    protected Collection<ManagedCategory> getCategories(final JSONArray array)
            throws JSONException {
        if (array == null) {
            return Arrays.asList();
        }

        final ManagedCategory[] res = new ManagedCategory[array.length()];

        for (int i = 0; i < array.length(); i++) {
            res[i] = getManagedCategory(array.getJSONObject(i));
        }

        return Arrays.asList(res);
    }

    /**
     * Utility method to convert a single managed category represented as
     * a JSON object into an actual {@link ManagedCategory}.
     *
     * @param object The object to be converted
     * @return A corresponding {@link ManagedCategory} instance
     * @throws JSONException If the category cannot be converted
     */
    protected ManagedCategory getManagedCategory(final JSONObject object)
            throws JSONException {
        return new ManagedCategory(
                object.getString("Id"),
                object.getString("Name"));
    }

}
