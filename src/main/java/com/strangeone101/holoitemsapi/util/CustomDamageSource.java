package com.strangeone101.holoitemsapi.util;

/**
 * A class for a damage source not provided by vanilla. This custom damage source can
 * have its own death message for players.
 */
public class CustomDamageSource {

    private String name;
    private boolean ignoreArmor;

    /**
     * Create a custom damage source
     * @param name The name of the damage source. E.g. `railgun`
     * @param ignoreArmor Whether the damage source should ALWAYS ignore armor
     */
    public CustomDamageSource(String name, boolean ignoreArmor) {
        this.name = name.replace(" ", "_");
        this.ignoreArmor = ignoreArmor;
    }

    /**
     * Should the custom damage source ignore armor
     * @return True if it ignores armor
     */
    public boolean doesIgnoreArmor() {
        return ignoreArmor;
    }

    /**
     * Get the name of the custom damage source
     * @return The name
     */
    public String getName() {
        return name;
    }
}
