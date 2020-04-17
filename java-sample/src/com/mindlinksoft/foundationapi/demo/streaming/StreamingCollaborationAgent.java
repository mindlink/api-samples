package com.mindlinksoft.foundationapi.demo.streaming;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mindlinksoft.foundationapi.demo.SimpleCollaborationAgent;

/**
 * An extension of {@link SimpleCollaborationAgent} which adds support for
 * streaming of events. This implementation will only support one concurrent
 * streaming request per agent.
 */
public class StreamingCollaborationAgent extends SimpleCollaborationAgent {

    /** The thread which our polling task is currently executing on. */
    private volatile Thread thread;

    /** Collection of registered event listeners. */
    private final List<EventListener> listeners
            = new CopyOnWriteArrayList<EventListener>();

    /**
     * Creates a new {@link StreamingCollaborationAgent}.
     *
     * @param baseUrl The base address for the Foundation API. The agent will
     * append method names automatically. For example, a base URL of
     * <code>http://api.company.com</code> will result in URLs constructed such
     * as <code>http://api.company.com/Authentication/v1/Tokens</code>.
     * @param username The username to give to the API when authenticating
     * @param password The username to give to the API when authenticating
     * @param agent The ID of the agent to use.
     */
    public StreamingCollaborationAgent(final String baseUrl,
            final String username, final String password, final String agent) {
        super(baseUrl, username, password, agent);
    }

    /**
     * Adds a new event listener to this agent. When any event is received
     * from any future streaming request, the listener will be notified.
     *
     * @see #removeEventListener(EventListener)
     * @param listener The listener to be added
     */
    public void addEventListener(final EventListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes an event listener from this agent. The listener will not be
     * notified of any future events.
     *
     * @see #addEventListener(EventListener)
     * @param listener The listener to be removed
     */
    public void removeEventListener(final EventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Begins streaming events from the API. Any previous streaming request from
     * this agent will be stopped. Streaming will otherwise continue until the
     * {@link #stopStreaming()} method is invoked on this agent.
     *
     * @param channels The channels to receive events for
     * (may be <code>null</code>)
     * @param regex The regular expression to apply to messages
     * (may be <code>null</code>)
     * @param types The types of events to retrieve
     */
    public void startStreaming(final String channels[],
            final String regex, final EventType ... types) {
        stopStreaming();
        thread = new Thread(new EventPoller(
                getCommaSeparatedString(types),
                getCommaSeparatedString(channels),
                regex == null ? "" : regex), "Streaming polling thread");
        thread.start();
    }

    /**
     * Stops any on-going streaming request.
     */
    public void stopStreaming() {
        if (thread != null) {
            final Thread oldThread = thread;
            thread = null;
            oldThread.interrupt();
        }
    }

    /**
     * Utility to return a comma-separated version of the given event types.
     *
     * @param types The types to be serialised
     * @return A comma-separated representation of the type's request forms
     */
    protected String getCommaSeparatedString(final EventType[] types) {
        final String[] items = new String[types.length];

        for (int i = 0; i < types.length; i++) {
            items[i] = types[i].getRequestForm();
        }

        return getCommaSeparatedString(items);
    }

    /**
     * Utility to return the given items as a comma-separated list.
     *
     * @param items The items to be serialised
     * @return A comma-separated version of the given items
     */
    protected String getCommaSeparatedString(final String[] items) {
        final StringBuilder builder = new StringBuilder();

        if (items != null) {
            for (String item : items) {
                if (builder.length() > 0) {
                    builder.append(',');
                }

                builder.append(item);
            }
        }

        return builder.toString();
    }

    /**
     * Calls the
     * {@link EventListener#eventReceived(StreamingCollaborationAgent, Event)}
     * method of each registered event listener.
     *
     * @param event The event to be passed to listeners
     */
    protected void fireEventReceived(Event event) {
        for (EventListener listener : listeners) {
            listener.eventReceived(this, event);
        }
    }

    /**
     * Constructs an {@link Event} subclass corresponding to the given object.
     *
     * @param object The object to be converted
     * @return A corresponding subclass of {@link Event}.
     * @throws JSONException If the event is malformed
     * @throws IllegalArgumentException If the event is of an unknown type
     */
    protected Event getEvent(final JSONObject object) throws JSONException {
        final String niceType = object.getString("__type").split(":")[0];

        final long eventId = object.getInt("EventId");
        final long timestamp = object.optLong("Time");

        if ("MessageEvent".equals(niceType)) {
            return new MessageEvent(eventId, timestamp,
                    object.getString("ChannelId"),
                    object.getString("Sender"),
                    object.optString("Subject"),
                    object.optString("Content"),
                    object.optString("MessageParts"),
                    object.optString("Token"));
        } else if ("MetaDataEvent".equals(niceType)) {
            return new MetaDataEvent(eventId, timestamp,
                    object.getString("Key"),
                    object.optString("Value"));
        } else if ("ChannelStateEvent".equals(niceType)) {
            return new ChannelStateEvent(eventId, timestamp,
                    object.getString("ChannelId"),
                    object.getBoolean("Active"));
        }

        throw new IllegalArgumentException("Unknown event type: " + niceType);
    }

    /**
     * Handles repeated polling for events.
     */
    private class EventPoller implements Runnable {

        /** The ID of the last event that was received. */
        private long lastEvent = 0;

        /** A comma-separated list of event types to poll for. */
        private final String types;

        /** A comma-separated list of channels to poll for. */
        private final String channels;

        /** A regular expression to apply to messages. */
        private final String regex;

        /**
         * Creates a new {@link EventPoller}.
         */
        public EventPoller(final String types, final String channels,
                final String regex) {
            this.types = types;
            this.channels = channels;
            this.regex = regex;
        }

        /**
         * Repeatedly polls the Events method until the {@link #thread}
         * property is changed. Retrieved events are passed to
         * {@link #fireEventReceived(Event)}.
         */
        @Override
        public void run() {
            while (Thread.currentThread() == thread) {
                try {
                    final JSONArray response = new JSONArray(getResponse(
                            "/Collaboration/v1/Events?last-event="
                            + lastEvent + "&types=" + types + "&channels="
                            + channels + "&regex=" + regex, "GET", null));

                    for (int i = 0; i < response.length(); i++) {
                        final Event event = getEvent(response.getJSONObject(i));
                        lastEvent = Math.max(lastEvent, event.getEventId());
                        fireEventReceived(event);
                    }
                } catch (IOException ex) {
                    // Hopefully just a transient issue - wait a short while
                    // and try again. This should ideally be logged, and
                    // should give up if the problem keeps happening.
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException iex) {
                        thread = null;
                    }
                } catch (JSONException ex) {
                    // Give up - if we can't parse the response we can't get the
                    // event ID, so future requests aren't going to work
                    // properly
                    thread = null;
                }
            }
        }

    }

}
