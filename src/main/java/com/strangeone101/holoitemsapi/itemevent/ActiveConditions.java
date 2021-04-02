package com.strangeone101.holoitemsapi.itemevent;

/**
 * Used in {@link ItemEvent} to specify when the item should be considered active
 * in order to trigger events
 */
public enum ActiveConditions {

    /**
     * When being held in the offhand or mainhand
     */
    HELD,
    /**
     * When being held in the main hand
     */
    MAINHAND,
    /**
     * When being held in the offhand
     */
    OFFHAND,
    /**
     * When being held in the hotbar
     */
    HOTBAR,
    /**
     * When being held anywhere in the inventory
     */
    INVENTORY,
    /**
     * When being worn as armor
     */
    EQUIPED,
    /**
     * When the item doesn't need to be active to trigger
     */
    NONE;

    /**
     * Tests if this condition matches the provided position of the item
     * @param position The item position
     * @return True if it matches
     */
    public boolean matches(Position position) {
        switch (this) {
            case MAINHAND:
                return position == Position.HELD;
            case OFFHAND:
                return position == Position.OFFHAND;
            case HELD:
                return position == Position.HELD || position == Position.OFFHAND;
            case HOTBAR:
                return position == Position.HOTBAR;
            case EQUIPED:
                return position == Position.ARMOR;
            case INVENTORY:
            case NONE:
                return true;
            default:
                return false;
        }
    }
}
