package com.strangeone101.holoitemsapi.itemevent;

/**
 * Used in {@link ItemEvent} to filter what type events should be triggered
 * when the item is active.
 */
public enum Target {
    /**
     * Targets the player causing the event
     */
    SELF,
    /**
     * Targets any event in the same world as the active item
     */
    WORLD,
    /**
     * Targets all events
     */
    ALL
}
