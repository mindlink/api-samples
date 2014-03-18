package com.mindlinksoft.foundationapi.demo.streaming;

/**
 * Specialised {@link Event} for describing meta-data changes.
 */
public class MetaDataEvent extends Event {

    private final String key;
    private final String value;

    /**
     * Creates a new instance of {@link MetaDataEvent}.
     */
    public MetaDataEvent(final long eventId, final long time, final String key,
            final String value) {
        super(eventId, time);

        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key that was changed.
     *
     * @return The meta-data key that has been modified
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the new value for the key.
     *
     * @return The new value of the meta-data key, or <code>null</code> if the
     * key has been deleted
     */
    public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "MetaDataEvent{super=" + super.toString() + ", key=" + key
                + ", value=" + value + '}';
    }

}
