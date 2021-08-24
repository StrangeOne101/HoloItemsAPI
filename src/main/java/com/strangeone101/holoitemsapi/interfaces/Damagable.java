package com.strangeone101.holoitemsapi.interfaces;

public interface Damagable {

    /**
     * The maximum durability this item should have
     * @return The max durability
     */
    int getMaxDurability();

    /**
     * When to display a warning message that the item is about
     * to break.
     *
     * If the number is below 0, it is used as a percentage. If
     * the number is higher, it is used as the durability remaining
     * before the player is warned.
     *
     * Examples:
     * 0.1 = warns at 10% durability
     * 20 = warns at 20 durability left
     * 0 = no warning.
     * @return
     */
    default float getBreakWarning() {
        return 0.1F;
    }

    boolean damageOnEntityHit();

    boolean damageOnBlockBreak();
}
