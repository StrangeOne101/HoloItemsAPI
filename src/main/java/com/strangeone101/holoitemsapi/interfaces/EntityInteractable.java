package com.strangeone101.holoitemsapi.interfaces;

import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An interface to run code when a player interacts (right clicks) with an entity
 */
public interface EntityInteractable {

    /**
     * Runs when the player interacts (right clicks) with an entity with the custom item
     * @param entity The entity interacted with
     * @param player The player interacting
     * @param item The custom item being used
     * @param stack The custom item stack
     * @return Whether to cancel the interaction. Should return true in almost all cases.
     */
    boolean onInteract(Entity entity, Player player, CustomItem item, ItemStack stack);
}
