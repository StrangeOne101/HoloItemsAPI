package com.strangeone101.holoitemsapi.itemevent;

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
