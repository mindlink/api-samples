package im.fcg.foundationapi.demo.streaming;

/**
 * Interface for objects interested in receiving events from a
 * {@link StreamingCollaborationAgent}.
 */
public interface EventListener {

    /**
     * Called when a new event has been received by the agent.
     *
     * @param agent The agent that received the event
     * @param event A subclass of {@link Event} describing the event
     */
    void eventReceived(StreamingCollaborationAgent agent, Event event);

}
