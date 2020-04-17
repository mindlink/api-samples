package com.mindlinksoft.foundationapi.demo.management;

/**
 * Represents a category that is available to the agent.
 */
public final class ManagedCategory {
    private final String id;
    private final String name;

    /**
     * Initializes a new instance of the {@link ManagedCategory} class.
     * @param id The ID of the category
     * @param name The name of the category
     */
    protected ManagedCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the ID of the category.
     * @return The ID of the category.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the name of the category.
     * @return The name of the category.
     */
    public String getName() {
        return this.name;
    }
}