package com.mindlinksoft.foundationapi.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mindlinksoft.foundationapi.demo.searchcriteria.MessageSearchCriteria;

/**
 * Agent for accessing the "simple" (non-streaming) collaboration methods.
 */
public class SimpleCollaborationAgent extends AuthenticatingAgent {

    /**
     * Creates a new {@link SimpleCollaborationAgent}.
     *
     * @param baseUrl The base address for the Foundation API. The agent will
     * append method names automatically. For example, a base URL of
     * <code>http://api.company.com</code> will result in URLs constructed such
     * as <code>http://api.company.com/Authentication/v1/Tokens</code>.
     * @param username The username to give to the API when authenticating
     * @param password The username to give to the API when authenticating
     * @param agent The ID of the agent to use.
     */
    public SimpleCollaborationAgent(final String baseUrl, final String username,
            final String password, final String agent) {
        super(baseUrl, username, password, agent);
    }

    /**
     * Sends a message to the specified channel.
     *
     * @param channelId The ID of the channel to send a message to
     * @param message The content of the message to send
     * @throws IOException If the request can't be constructed or transmitted
     */
    public void sendMessage(final String channelId, final String message)
            throws IOException {
        sendMessage(channelId, null, message, false, false, false);
    }

    /**
     * Sends a message with the specified metadata to the specified channel.
     *
     * @param channelId The ID of the channel to send a message to
     * @param message The content of the message to send
     * @param hasClassification A value indicating whether or not the message has a classification
     * @param hasSecurityContext A value indicating whether or not the message has a security context
     */
    public void sendMessageWithMetadata(
            final String channelId,
            final String message,
            final boolean hasClassification,
            final boolean hasSecurityContext) throws IOException {
        sendMessage(channelId, null, message, false, hasClassification, hasSecurityContext);
    }

    /**
     * Sends an alert message to the specified channel.
     *
     * @param channelId The ID of the channel to send a message to
     * @param message The content of the message to send
     * @throws IOException If the request can't be constructed or transmitted
     */
    public void sendAlert(final String channelId, final String message)
            throws IOException {
        sendMessage(channelId, null, message, true, false, false);
    }

    /**
     * Sends a story message to the specified channel.
     *
     * @param channelId The ID of the channel to send a message to
     * @param subject The subject of the message
     * @throws IOException If the request can't be constructed or transmitted
     */
    public void sendStory(final String channelId, final String subject,
            final String body) throws IOException {
        sendMessage(channelId, subject, body, false, false, false);
    }

    /**
     * Sends a selection of predefined message parts to the specified channel.
     *
     * Note: This feature only applies to API v18.6 and above.
     *
     * @param channelId The ID of the channel to send a message to
     * @throws IOException If the request can't be constructed or transmitted
     */
    public void sendMessageParts(final String channelId) throws IOException {
        try {
            final JSONArray messageParts = new JSONArray();

            final JSONOrderedObject plainTextMessagePart = new JSONOrderedObject();
            plainTextMessagePart.put("__type", "PlainTextMessagePart:http://schemas.fcg.im/foundation/v1/collaboration");
            plainTextMessagePart.put("Text", "This is a test message");

            final JSONOrderedObject hyperlinkMessagePart = new JSONOrderedObject();
            hyperlinkMessagePart.put("__type", "HyperlinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration");
            hyperlinkMessagePart.put("Text", "A Hyperlink");
            hyperlinkMessagePart.put("Url", "http://www.example.com");

            final JSONOrderedObject channelLinkMessagePart = new JSONOrderedObject();
            channelLinkMessagePart.put("__type", "ChannelLinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration");
            channelLinkMessagePart.put("ChannelName", "Channel Name");
            channelLinkMessagePart.put("ChannelId", channelId);

            final JSONOrderedObject hashtagMessagePart = new JSONOrderedObject();
            hashtagMessagePart.put("__type", "HashtagMessagePart:http://schemas.fcg.im/foundation/v1/collaboration");
            hashtagMessagePart.put("Hashtag", "#hashtag");

            final JSONOrderedObject codeBlockMessagePart = new JSONOrderedObject();
            codeBlockMessagePart.put("__type", "CodeBlockMessagePart:http://schemas.fcg.im/foundation/v1/collaboration");
            codeBlockMessagePart.put("CodeBlock", "System.out.println(\"Hello World!\")");

            messageParts.put(plainTextMessagePart);
            messageParts.put(hyperlinkMessagePart);
            messageParts.put(channelLinkMessagePart);
            messageParts.put(hashtagMessagePart);
            messageParts.put(codeBlockMessagePart);

            final JSONObject payload = new JSONObject();
            payload.put("MessageParts", messageParts);

            getResponse("/Collaboration/v1/Channels/" + channelId + "/Messages", "POST", payload.toString());
        } catch (JSONException ex) {
            throw new IOException("Unable to construct JSON payload", ex);
        }
    }

    /**
     * Sends a message to the specified channel.
     *
     * @param channelId The ID of the channel to send a message to
     * @param subject The subject of the story or <code>null</code> for a non-
     * story message
     * @param message The content of the message to send
     * @param alert Whether to send the message as an alert or not
     * @param hasClassification A value indicating whether or not the message has a classification
     * @param hasSecurityContext A value indicating whether or not the message has a security context
     * @throws IOException If the request can't be constructed or transmitted
     */
    protected void sendMessage(final String channelId,
            final String subject, final String message,
            final boolean alert, boolean hasClassification,
            boolean hasSecurityContext) throws IOException {
        try {
            final JSONObject payload = new JSONObject();
            payload.put("IsAlert", alert);
            payload.put("Subject", subject);
            payload.put("Text", message);

            if (hasClassification) {
                final JSONOrderedObject classification = new JSONOrderedObject();
                classification.put("__type", "Classification:http://schemas.fcg.im/foundation/v1/collaboration");
                classification.put("Token", "primary.us.confidential,disseminations.display,disseminations.display.identity-can");

                payload.put("Classification", classification);
            }

            if (hasSecurityContext) {
                final JSONOrderedObject securityContext = new JSONOrderedObject();
                securityContext.put("__type", "SecurityContext:http://schemas.fcg.im/foundation/v1/collaboration");
                securityContext.put("Id", "coi2");

                final JSONOrderedObject[] securityContexts = { securityContext };

                payload.put("SecurityContexts", securityContexts);
            }

            getResponse("/Collaboration/v1/Channels/" + channelId + "/Messages",
                    "POST", payload.toString());
        } catch (JSONException ex) {
            throw new IOException("Unable to construct JSON payload", ex);
        }
    }

    /**
     * Retrieves the metadata associated with the agent.
     *
     * @return A map containing arbitrary metadata assigned to the agent.
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public Map<String, String> getMetadata() throws IOException {
        try {
            final JSONArray response = new JSONArray(
                    getResponse("/Collaboration/v1/MetaData", "GET", null));
            return getMap(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Retrieves the set of all channels that the current agent is provisioned
     * for.
     *
     * @throws IOException If the request could not be completed or the response
     * not parsed
     * @returns A collection of {@link Channel} instances corresponding to the
     * agent's provisioned channels
     */
    public Collection<Channel> getChannels() throws IOException {
        final List<Channel> channels
                = new ArrayList<Channel>();

        try {
            final JSONArray response = new JSONArray(getResponse(
                    "/Collaboration/v1/Channels",
                    "GET", null));

            for (int i = 0; i < response.length(); i++) {
                channels.add(getChannelInformation(
                        (JSONObject) response.get(i)));
            }
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }

        return channels;
    }

    /**
     * Searches a set of channels for messages matching the search criteria.
     *
     * @param criteria A {@link MessageSearchCriteria} implementation describing
     * the search criteria.
     * @throws IOException If the request could not be completed or the response
     * not parsed
     * @returns A collection of {@link SearchResultSet} instances corresponding
     * to the search results
     */
    public Collection<SearchResultSet> searchChannels(
            final MessageSearchCriteria criteria) throws IOException {
        final List<SearchResultSet> entries = new ArrayList<SearchResultSet>();

        try {
            final JSONObject object = new JSONObject();
            criteria.putData(object);
            final JSONArray response = new JSONArray(
                    getResponse("/Collaboration/v1/Channels/Search", "POST",
                    object.toString()));

            for (int i = 0; i < response.length(); i++) {
                entries.add(getSearchResultSet((JSONObject) response.get(i)));
            }
        } catch (JSONException ex) {
            throw new IOException("Unable to construct JSON payload or "
                    + "parse response", ex);
        }

        return entries;
    }

    /**
     * Retrieves information for the channel with the channel ID. Information
     * may only be retrieved about channels that the agent is provisioned for.
     *
     * @param channelId The ID of the channel to retrieve information for
     * @return A {@link Channel} object describing the channel
     * @see #getChannels()
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public Channel getChannelInformation(final String channelId)
            throws IOException {
        try {
            final JSONObject response = new JSONObject(getResponse(
                    "/Collaboration/v1/Channels/" + channelId,
                    "GET", null));

            return getChannelInformation(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Retrieves the current state of the specified channel.
     *
     * @param channelId The ID of the channel to retrieve state for
     * @return A {@link ChannelState} object describing the channel
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public ChannelState getChannelState(final String channelId)
            throws IOException {
        try {
            final JSONObject response = new JSONObject(getResponse(
                    "/Collaboration/v1/Channels/" + channelId + "/State",
                    "GET", null));

            return getChannelState(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Retrieves the most recent history of the specified channel.
     *
     * @param channelId The ID of the channel to retrieve history for
     * @param number The maximum number of messages to retrieve
     * @param beforeToken The token from which to begin the history, or {@code null} to get the latest history.
     * @return A list of most recent {@link Message}s in the channel
     * @throws IOException If the request could not be completed or the response
     * not parsed
     */
    public List<Message> getChannelHistory(final String channelId,
            final int number, final String beforeToken) throws IOException {
        try {
            final String beforeParam = beforeToken == null ? "" : "&before=" + beforeToken;
            final JSONArray response = new JSONArray(getResponse(
                    "/Collaboration/v1/Channels/" + channelId
                    + "/Messages?take=" + number
                    + beforeParam,
                    "GET", null));
            return getMessages(response);
        } catch (JSONException ex) {
            throw new IOException("Unable to deserialise JSON response", ex);
        }
    }

    /**
     * Updates the channel agent state.
     * @param isComposing A value indicating whether the agent is composing.
     * @throws IOException If the request could not be completed.
     */
    public void updateChannelAgentState(final String channelId, final Boolean isComposing) throws IOException {
    	try {
        	JSONOrderedObject channelAgentState = new JSONOrderedObject();
        	channelAgentState.put("IsComposing", isComposing.toString());

        	getResponse(
                    "/Collaboration/v1/Channels/" + channelId
                    + "/Me",
                    "POST", channelAgentState.toString());
        } catch (JSONException ex) {
            throw new IOException("Unable to construct JSON payload", ex);
        }
    }

    /**
     * Constructs a collection of {@link Message}s from the given JSON array.
     *
     * @see #getMessage(org.json.JSONObject)
     * @param array The JSON array to be converted
     * @return A collection of corresponding Message instances
     * @throws JSONException If the object fails to contain required properties
     */
    protected List<Message> getMessages(final JSONArray array) throws JSONException {
        final List<Message> messages = new ArrayList<Message>();
        for (int i = 0; i < array.length(); i++) {
            messages.add(getMessage((JSONObject) array.get(i)));
        }
        return messages;
    }

    /**
     * Constructs a {@link SearchResultSet} object from the given JSON object.
     *
     * @param object The JSON object to be converted
     * @return A corresponding Message instance
     * @throws JSONException If the object fails to contain required properties
     */
    protected SearchResultSet getSearchResultSet(final JSONObject object) throws JSONException {
        return new SearchResultSet(
                object.getString("ChannelId"),
                object.getInt("Count"),
                object.getString("MaxMessageId"),
                getMessages(object.getJSONArray("Messages")),
                object.getString("MinMessageId")
                );
    }

    /**
     * Constructs a {@link Channel} object from the given JSON object.
     *
     * @param object The JSON object to be converted
     * @return A corresponding Channel instance
     * @throws JSONException If the object fails to contain required properties
     */
    protected Channel getChannelInformation(final JSONObject object) throws JSONException {
        return new Channel(
                object.optBoolean("CanAcceptFiles"),
                object.optString("Description"),
                object.getString("DisplayName"),
                object.optString("EmailAddress"),
                object.getString("Id"),
                object.optBoolean("IsReadOnly"),
                getMap(object.optJSONArray("MetaData")),
                object.optString("Subject"));
    }

    /**
     * Constructs a {@link Message} object from the given JSON object.
     *
     * @param object The JSON object to be converted
     * @return A corresponding Message instance
     * @throws JSONException If the object fails to contain required properties
     */
    protected Message getMessage(final JSONObject object) throws JSONException {
        return new Message(
                object.getString("Id"),
                object.optBoolean("IsAlert"),
                object.getString("SenderId"),
                object.optString("SenderAlias"),
                object.optString("ChannelId"),
                object.optString("Subject"),
                object.getString("Text"),
                object.optString("MessageParts"),
                object.getLong("Timestamp"),
                object.optString("Token"),
                object.optString("Classification"),
                object.optString("SecurityContext"),
                object.optString("DataAttributes"));
    }

    /**
     * Constructs a {@link ChannelState} object from the given JSON object.
     *
     * @param object The JSON object to be converted
     * @return A corresponding ChannelState instance
     * @throws JSONException If the object fails to contain required properties
     */
    protected ChannelState getChannelState(final JSONObject object) throws JSONException {
        return new ChannelState(
                object.optString("Subject"),
                PresenceState.forValue(object.getInt("PresenceState")),
                object.optString("PresenceText"));
    }

}
