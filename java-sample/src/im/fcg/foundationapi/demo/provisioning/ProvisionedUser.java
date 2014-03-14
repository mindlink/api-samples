package im.fcg.foundationapi.demo.provisioning;

/**
 * Simple representation of a provisioned user as returned by the API.
 */
public class ProvisionedUser {

    private final String id;

    private final String username;

    /**
     * Creates a new {@link ProvisionedUser}.
     *
     * @param id The unique ID of the user
     * @param username The username the user will use to authenticate
     */
    public ProvisionedUser(final String id, final String username) {
        this.id = id;
        this.username = username;
    }

    /**
     * Gets the unique ID of the user.
     *
     * @return The user's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the username which the user will use to authenticate.
     *
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ProvisionedUser{id=" + id + ", username=" + username + '}';
    }

}
