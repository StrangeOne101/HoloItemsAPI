package com.strangeone101.holoitemsapi.interfaces;

import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An interface for custom items that can be used as weapons
 */
public interface Swingable {

    /**
     * Fired when the player swings this item and doesn't hit anything
     * @param player The player
     * @param item The custom item
     * @param stack The itemstack
     */
    void swing(Player player, CustomItem item, ItemStack stack);

    /**
     * Fired when the player swings this item at an entity and hits them
     * @param entityHit The entity hit
     * @param player The player
     * @param item The custom item
     * @param stack The itemstack
     * @param damage The original damage
     * @return The damage
     */
    double hit(Entity entityHit, Player player, CustomItem item, ItemStack stack, double damage);
}
