package com.strangeone101.holoitemsapi.interfaces;

import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An interface to make the custom item edible
 */
public interface Edible {

    /**
     * Get how much hunger this item restores when eaten
     * @return The hunger amount
     */
    int getHungerAmount();

    /**
     * Get how much saturation this item restores when eaten
     * @return The saturation amount
     */
    float getSaturationAmount();

    /**
     * What to do when the item is eaten. Must be overriden as it defaults to nothing.
     * @param player The player that ate the item
     * @param item The custom item eaten
     * @param stack The itemstack of the custom item
     */
    default void onEat(Player player, CustomItem item, ItemStack stack) { }

    /**
     * The standard amount of time to eat items
     */
    static long NORMAL_LENGTH = 1600;
    /**
     * Used for items that are eaten very fast like kelp
     */
    static long SHORT_LENGTH = 800;
    /**
     * Used for items that take extra long to eat
     */
    static long LONG_LENGTH = 3200;

    /**
     * How long it takes to eat the item
     * @return The eat duration (in milliseconds)
     */
    default long getEatDuration() {
        return NORMAL_LENGTH;
    }

    /**
     * Whether this item can be eaten when the player is at full hunger
     * @return If it can be eaten at full hunger
     */
    default boolean eatWhenFull() {
        return false;
    }

}
