package com.strangeone101.holoitemsapi.itemevent;

/**
 * The cached position of an item that needs to trigger bukkit events
 */
public enum Position {
    /**
     * In the main hand
     */
    HELD,
    /**
     * In the off hand
     */
    OFFHAND,
    /**
     * Anywhere else in the hotbar
     */
    HOTBAR,
    /**
     * Worn as armomr
     */
    ARMOR,
    /**
     * Anywhere else in the inventory
     */
    INVENTORY,
    /**
     * Not in the inventory but still cached. Internal use only.
     */
    OTHER
}

